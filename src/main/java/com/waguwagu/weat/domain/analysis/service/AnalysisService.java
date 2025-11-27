package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.event.AnalysisStartEvent;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisResultDetailNotFoundException;
import com.waguwagu.weat.domain.analysis.exception.MemberAlreadySubmitSettingForMemberIdException;
import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundException;
import com.waguwagu.weat.domain.analysis.model.dto.*;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.policy.AnalysisSettingSubmitPolicy;
import com.waguwagu.weat.domain.analysis.policy.AnalysisStartPolicy;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.exception.CategoryTagNotFoundException;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class AnalysisService {

    private final AIServiceAdaptor aiServiceAdaptor;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;
    private final CategoryTagRepository categoryTagRepository;
    private final AnalysisResultLikeRepository analysisResultLikeRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AnalysisStartPolicy analysisStartPolicy;
    private final AnalysisSettingSubmitPolicy analysisSettingSubmitPolicy;

    @Value("${ai.service.uri.validation}")
    private String validationUri;

    private static final Duration AI_TIMEOUT = Duration.ofSeconds(60);

    public GetAnalysisStatusDTO.Response getAnalysisStatus(@NotNull String groupId) {

        // 그룹 정보 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // 그룹의 분석상태 조회
        AnalysisStatus analysisStatus = analysisRepository.findByGroupGroupId(groupId)
                .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(groupId))
                .getAnalysisStatus();

        // 그룹내의 설정 제출 수
        Long submittedCount = analysisSettingRepository.countAnalysisSettingByGroupId(groupId);

        // 분석시작가능 여부 평가
        AnalysisStartPolicy.Result analysisStartPolicyEvaluateteResult = analysisStartPolicy.evaluate(groupId);

        return GetAnalysisStatusDTO.Response.builder()
                .groupId(group.getGroupId())
                .isSingleMemberGroup(group.isSingleMemberGroup())
                .submittedCount(submittedCount)
                .isAnalysisStartConditionSatisfied(analysisStartPolicyEvaluateteResult.isSatisfied())
                .analysisStatus(analysisStatus)
                .build();
    }


    // 멤버별 분석 설정 제출 여부 조회
    public IsMemberSubmitAnalysisSettingDTO.Response isMemberSubmitAnalysisSetting(Long memberId) {
        // 멤버 정보 조회
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
        // 멤버 정보 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(requestDto.getMemberId()));

        // 멤버가 분석 설정을 제출할 수 있는 상태인지 검증
        analysisSettingSubmitPolicy.validate(member.getMemberId());

        // 분석 정보 조회
        Analysis analysis = analysisRepository.findByGroupGroupId(member.getGroup().getGroupId())
                .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(member.getGroup().getGroupId()));

        // 설정 정보 생성
        AnalysisSetting analysisSetting = AnalysisSetting.builder()
                .analysis(analysis)
                .member(member)
                .build();

        // 설정 정보 저장
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

            CategoryTag categoryTag = categoryTagRepository.findById(categorySettingDTO.getCategoryTagId())
                    .orElseThrow(() -> new CategoryTagNotFoundException(categorySettingDTO.getCategoryTagId()));

            CategorySetting categorySetting = CategorySetting.builder()
                    .analysisSetting(analysisSetting)
                    .category(categoryTag.getCategory())
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

    public AnalysisStartDTO.Response analysisStart(AnalysisStartDTO.Request request) {

        // 그룹 조회
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

        // 분석시작조건 충족 여부 확인
        analysisStartPolicy.validate(group.getGroupId());

        // 그룹에 대한 분석정보 조회
        Analysis analysis = analysisRepository.findByGroupGroupId(group.getGroupId())
                .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(group.getGroupId()));

        // "진행중" 상태로 변경
        analysis.setAnalysisStatus(AnalysisStatus.IN_PROGRESS);

        // 그룹에 속한 멤버들의 분석 설정 일괄 조회
        List<MemberAnalysisSettingDTO> groupMemberSettings =
                analysisSettingRepository.findMemberAnalysisSettingsByGroupId(group.getGroupId());

        // AI 분석 시작 요청에 사용되는 객체로 변환
        List<AIAnalysisDTO.Request.MemberSetting> memberSettingList = groupMemberSettings.stream()
                .map(dto -> AIAnalysisDTO.Request.MemberSetting.builder()
                        .memberId(dto.getMemberId())
                        .xPosition(dto.getXPosition())
                        .yPosition(dto.getYPosition())
                        .roadnameAddress(dto.getRoadnameAddress())
                        .inputText(dto.getInputText()) // null이어도 그대로 전달
                        .categoryList(dto.getCategorySettings().stream()
                                .map(c -> AIAnalysisDTO.Request.MemberSetting.CategorySetting.builder()
                                        .categoryId(c.getCategoryId())
                                        .categoryName(c.getCategoryName())
                                        .categoryTagId(c.getCategoryTagId())
                                        .categoryTagName(c.getCategoryTagName())
                                        .isPreferred(c.getIsPreferred())
                                        .build())
                                .toList())
                        .build())
                .toList();

        // 트랜잭션 커밋 이후 AI 분석 요청
        eventPublisher.publishEvent(new AnalysisStartEvent(
                group.getGroupId(),
                analysis.getAnalysisId(),
                memberSettingList
        ));

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
