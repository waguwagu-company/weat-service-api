package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.exception.*;
import com.waguwagu.weat.domain.analysis.model.dto.*;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.exception.CategoryNotFoundException;
import com.waguwagu.weat.domain.category.exception.CategoryTagNotFoundException;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisService {

    private final AIServiceAdaptor aiServiceAdaptor;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;
    private final AnalysisAsyncExecutor analysisAsyncExecutor;
    private final TextInputSettingRepository textInputSettingRepository;
    private final LocationSettingRepository locationSettingRepository;
    private final CategorySettingRepository categorySettingRepository;
    private final CategoryTagRepository categoryTagRepository;
    private final AnalysisResultLikeRepository analysisResultLikeRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;


    @Value("${ai.service.uri.validation}")
    private String validationUri;

    private static final Duration AI_TIMEOUT = Duration.ofSeconds(60);

    // 분석 시작가능조건 충족여부 및 분석상태 조회
    public GetAnalysisStatusDTO.Response getAnalysisStatus(String groupId) {

        final int GROUP_CRITERIA = 2;
        final int SINGLE_CRITERIA = 1;

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        String analysisStatus = analysisRepository.findByGroupGroupId(groupId)
                .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(groupId))
                .getAnalysisStatus().toString();

        List<Member> memberList = memberRepository.findAllByGroupGroupId(groupId);

        int submittedCount = (int) memberList.stream()
                .filter(member -> analysisSettingRepository.existsByMemberMemberId(member.getMemberId()))
                .count();

        int criteria = group.isSingleMemberGroup() ? SINGLE_CRITERIA : GROUP_CRITERIA;
        boolean isSatisfied = submittedCount >= criteria;

        return GetAnalysisStatusDTO.Response.builder()
                .groupId(group.getGroupId())
                .isSingleMemberGroup(group.isSingleMemberGroup())
                .submittedCount(submittedCount)
                .isAnalysisStartConditionSatisfied(isSatisfied)
                .analysisStatus(analysisStatus)
                .build();
    }


    // 멤버별 분석 설정 제출 여부 조회
    public IsMemberSubmitAnalysisSettingDTO.Response isMemberSubmitAnalysisSetting(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return IsMemberSubmitAnalysisSettingDTO.Response
                .builder()
                .groupId(member.getGroup().getGroupId())
                .memberId(member.getMemberId())
                .isSubmitted(analysisSettingRepository.existsByMemberMemberId(memberId))
                .build();
    }

    // 멤버별 분석 설정 제출
    public SubmitAnalysisSettingDTO.Response submitAnalysisSetting(SubmitAnalysisSettingDTO.Request requestDto) {
        // 회원 정보 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(requestDto.getMemberId()));

        // 이미 제출한 회원인 경우
        if (isMemberSubmitAnalysisSetting(requestDto.getMemberId()).isSubmitted()) {
            throw new MemberAlreadySubmitSettingForMemberIdException(member.getMemberId());
        }

        // 분석 정보 조회
        Analysis analysis = analysisRepository.findByGroupGroupId(member.getGroup().getGroupId())
                .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(member.getGroup().getGroupId()));

        // 설정 정보 생성
        AnalysisSetting analysisSetting = AnalysisSetting.builder()
                .analysis(analysis)
                .member(member)
                .build();

        analysisSettingRepository.save(analysisSetting);

        // 위치 설정
        LocationSetting locationSetting = LocationSetting.builder()
                .analysisSetting(analysisSetting)
                .xPosition(requestDto.getLocationSetting().getXPosition())
                .yPosition(requestDto.getLocationSetting().getYPosition())
                .roadnameAddress(requestDto.getLocationSetting().getRoadnameAddress())
                .build();

        analysisSettingDetailRepository.save(locationSetting);

        // 카테고리 설정
        for (SubmitAnalysisSettingDTO.Request.CategorySetting categorySettingDTO : requestDto.getCategorySettingList()) {
            Category category = categoryRepository.findById(categorySettingDTO.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(categorySettingDTO.getCategoryId()));

            CategoryTag categoryTag = categoryTagRepository.findById(categorySettingDTO.getCategoryTagId())
                    .orElseThrow(() -> new CategoryTagNotFoundException(categorySettingDTO.getCategoryTagId()));

            CategorySetting categorySetting = CategorySetting.builder()
                    .analysisSetting(analysisSetting)
                    .category(category)
                    .categoryTag(categoryTag)
                    .isPreferred(categorySettingDTO.getIsPreferred())
                    .build();

            analysisSettingDetailRepository.save(categorySetting);
        }

        // 텍스트 입력 설정
        TextInputSetting textInputSetting = TextInputSetting.builder()
                .analysisSetting(analysisSetting)
                .inputText(requestDto.getTextInputSetting().getInputText())
                .build();

        analysisSettingDetailRepository.save(textInputSetting);

        return SubmitAnalysisSettingDTO.Response.builder()
                .memberId(member.getMemberId())
                .analysisSettingId(analysisSetting.getAnalysisSettingId())
                .build();
    }

    // TODO: 개발 진행중, AI 분석 서비스 응답 형식 정해지면 재개
    public AnalysisStartDTO.Response analysisStart(AnalysisStartDTO.Request request) {

        var groupId = request.getGroupId();

        // 그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));


        var analysisStartAvailable = getAnalysisStatus(groupId);

        if (!analysisStartAvailable.getAnalysisStatus().equals(AnalysisStatus.NOT_STARTED.toString())) {
            throw new AnalysisAlreadyStartedForGroupIdException(groupId);
        }

        if (!analysisStartAvailable.getIsAnalysisStartConditionSatisfied()) {
            throw new AnalysisConditionNotSatisfiedForGroupIdException(groupId);
        }

        // 그룹에 대한 분석정보 조회
        Analysis analysis = analysisRepository.findByGroupGroupId(group.getGroupId())
                .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(group.getGroupId()));

        // 이미 진행중인 분석인지 확인
        if (!analysis.getAnalysisStatus().equals(AnalysisStatus.NOT_STARTED)) {
            // 이미 진행중이라면 208 응답 반환
            throw new AnalysisAlreadyStartedForGroupIdException(groupId);
        }

        // 진행중이지 않다면, "진행중" 상태로 변경
        analysis.setAnalysisStatus(AnalysisStatus.IN_PROGRESS);

        // 그룹 내의 모든 멤버 조회
        List<Member> groupMemberList = memberRepository.findAllByGroupGroupId(group.getGroupId());

        List<AIAnalysisDTO.Request.MemberSetting> memberSettingList = new ArrayList<>();


        // 각 멤버별 설정 값 조회
        for (Member groupMember : groupMemberList) {
            // 위치 설정
            LocationSetting locationSetting = locationSettingRepository.findByMemberId(groupMember.getMemberId());

            // 카테고리 설정
            List<CategorySetting> categorySettingList = categorySettingRepository.findAllByMemberId(groupMember.getMemberId());

            List<AIAnalysisDTO.Request.MemberSetting.CategorySetting> memberSettingCategoryList = new ArrayList<>();

            for (CategorySetting categorySetting : categorySettingList) {
                memberSettingCategoryList.add(
                        AIAnalysisDTO.Request.MemberSetting.CategorySetting.builder()
                                .categoryId(categorySetting.getCategory().getCategoryId())
                                .categoryName(categorySetting.getCategory().getCategoryName())
                                .categoryTagId(categorySetting.getCategoryTag().getCategoryTagId())
                                .categoryTagName(categorySetting.getCategoryTag().getCategoryTagName())
                                .isPreferred(categorySetting.getIsPreferred())
                                .build()
                );
            }

            // 입력 설정
            TextInputSetting textInputSetting = textInputSettingRepository.findByMemberId(groupMember.getMemberId());

            AIAnalysisDTO.Request.MemberSetting memberSetting =
                    AIAnalysisDTO.Request.MemberSetting.builder()
                            .memberId(groupMember.getMemberId())
                            .xPosition(locationSetting.getXPosition())
                            .yPosition(locationSetting.getYPosition())
                            .categoryList(memberSettingCategoryList)
                            .inputText(textInputSetting.getInputText())
                            .build();

            memberSettingList.add(memberSetting);
        }


        // 비동기로 AI 분석서비스에 분석 요청
        analysisAsyncExecutor.startAnalysisAsync(AIAnalysisDTO.Request.builder()
                .groupId(group.getGroupId())
                .analysisId(analysis.getAnalysisId())
                .memberSettingList(memberSettingList)
                .build());

        return AnalysisStartDTO.Response.builder()
                .groupId(group.getGroupId())
                .analysisId(analysis.getAnalysisId())
                .analysisStatus(analysis.getAnalysisStatus().toString())
                .build();
    }

    public Mono<ValidationDTO.Response> validateInput(ValidationDTO.Request request) {
        return aiServiceAdaptor.postJson(validationUri, request, ValidationDTO.Response.class, AI_TIMEOUT);
    }


    // 분석결과상세(장소)별 좋아요 토글 기능
    public ToggleAnalysisResultDetailLikeDTO.Response toggleAnalysisResultDetailLike(ToggleAnalysisResultDetailLikeDTO.Request request) {

        Long memberId = request.getMemberId();
        Long analysisResultDetailId = request.getAnalysisResultDetailId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Optional<AnalysisResultLike> likeOpt =
                analysisResultLikeRepository.findByAnalysisResultDetailIdAndMemberId(analysisResultDetailId, memberId);

        if (likeOpt.isPresent()) {
            analysisResultLikeRepository.delete(likeOpt.get());
            return ToggleAnalysisResultDetailLikeDTO.Response.builder()
                    .analysisResultDetailId(analysisResultDetailId)
                    .memberId(memberId)
                    .isLiked(false)
                    .build();
        }

        AnalysisResultDetail detail = analysisResultDetailRepository.getReferenceById(analysisResultDetailId);

        AnalysisResultLike like = AnalysisResultLike.builder()
                .analysisResultDetail(detail)
                .member(member)
                .build();

        analysisResultLikeRepository.save(like);

        return ToggleAnalysisResultDetailLikeDTO.Response.builder()
                .analysisResultDetailId(analysisResultDetailId)
                .memberId(memberId)
                .isLiked(true)
                .build();
    }

    /**
     * 분석결과상세별 좋아요 개수 조회
     */
    public GetAnalysisResultLikeCountDTO.Response getAnalysisResultLikeCount(Long analysisResultDetailId) {

        AnalysisResultDetail analysisResultDetail =
                analysisResultDetailRepository.findById(analysisResultDetailId)
                        .orElseThrow(() -> new AnalysisResultDetailNotFoundException(analysisResultDetailId));

        Long count = analysisResultLikeRepository.countByAnalysisResultDetail(analysisResultDetail);

        return GetAnalysisResultLikeCountDTO.Response.builder()
                .analysisResultDetailId(analysisResultDetailId)
                .likeCount(count)
                .build();
    }


    /**
     * 멤버가 특정 분석결과상세에 좋아요를 눌렀는지 상태 조회
     */
    public GetAnalysisResultLikeStatusByDetailDTO.Response getAnalysisResultLikeStatusByDetail(Long analysisResultDetailId, Long memberId) {

        return GetAnalysisResultLikeStatusByDetailDTO.Response.builder()
                .analysisResultDetailId(analysisResultDetailId)
                .memberId(memberId)
                .isLiked(analysisResultLikeRepository.findByAnalysisResultDetailIdAndMemberId(analysisResultDetailId, memberId).isPresent())
                .build();
    }


}
