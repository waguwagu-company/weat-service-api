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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisService {

    private final PlaceRepository placeRepository;
    private final AIServiceAdaptor aiServiceAdaptor;

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final AnalysisRepository analysisRepository;
    private final PlaceImageRepository placeImageRepository;
    private final AnalysisBasisRepository analysisBasisRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;
    private final AnalysisAsyncExecutor analysisAsyncExecutor;
    private final TextInputSettingRepository textInputSettingRepository;
    private final LocationSettingRepository locationSettingRepository;
    private final CategorySettingRepository categorySettingRepository;
    private final CategoryTagRepository categoryTagRepository;

    // 분석 시작가능조건 충족여부 및 분석상태 조회
    public IsAnalysisStartAvailableDTO.Response isAnalysisStartAvailable(String groupId) {

        final int GROUP_CRITERIA = 2;
        final int SINGLE_CRITERIA = 1;

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        boolean isAnalysisStarted = analysisRepository.findByGroupGroupId(groupId)
                .map(analysis -> !analysis.getAnalysisStatus().equals(AnalysisStatus.NOT_STARTED))
                .orElse(false);

        List<Member> memberList = memberRepository.findAllByGroupGroupId(groupId);

        int submittedCount = (int) memberList.stream()
                .filter(member -> analysisSettingRepository.existsByMemberMemberId(member.getMemberId()))
                .count();

        int criteria = group.isSingleMemberGroup() ? SINGLE_CRITERIA : GROUP_CRITERIA;
        boolean isSatisfied = submittedCount >= criteria;

        return IsAnalysisStartAvailableDTO.Response.builder()
                .groupId(group.getGroupId())
                .submittedCount(submittedCount)
                .isAnalysisStartConditionSatisfied(isSatisfied)
                .isAnalysisStarted(isAnalysisStarted)
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

    @Transactional
    // 멤버별 분석 설정 제출
    public SubmitAnalysisSettingDTO.Response submitAnalysisSetting(SubmitAnalysisSettingDTO.Request requestDto) {
        // 회원 정보 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(requestDto.getMemberId()));

        // 이미 제출한 회원인 경우
        if (isMemberSubmitAnalysisSetting(requestDto.getMemberId()).isSubmitted()) {
            throw new MemberAlreadySubmitSettingForMemberIdException(member.getMemberId());
        }

        // 분석 정보 조회, 없는 경우 생성
        Analysis analysis = analysisRepository.findByGroupGroupId(member.getGroup().getGroupId())
                .orElseGet(() -> {
                    Analysis newAnalysis = Analysis.builder()
                            .group(member.getGroup())
                            .build();
                    return analysisRepository.save(newAnalysis);
                });

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
    @Transactional
    public AnalysisStartDTO.Response analysisStart(AnalysisStartDTO.Request request) {

        var groupId = request.getGroupId();

        // 그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));


        var analysisStartAvailable = isAnalysisStartAvailable(groupId);

        if (analysisStartAvailable.getIsAnalysisStarted()) {
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
                .memberSettingList(memberSettingList)
                .build());

        return AnalysisStartDTO.Response.builder()
                .groupId(group.getGroupId())
                .analysisId(analysis.getAnalysisId())
                .analysisStatus(analysis.getAnalysisStatus().toString())
                .build();
    }

    public ValidationDTO.Response validateInput(ValidationDTO.Request request) {
        return aiServiceAdaptor.requestValidation(request);
    }
}
