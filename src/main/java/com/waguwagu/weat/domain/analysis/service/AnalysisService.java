package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.exception.AnalysisAlreadyStartedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundException;
import com.waguwagu.weat.domain.analysis.model.dto.*;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.AnalysisRepository;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingDetailRepository;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingRepository;
import com.waguwagu.weat.domain.category.exception.CategoryNotFoundException;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisService {

    private final Executor analysisServiceExcutor = Executors.newFixedThreadPool(10);

    private final AIServiceAdaptor aiServiceAdaptor;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;


    // 분석 시작가능조건 충족여부 및 분석상태 조회
    public IsAnalysisStartAvailableDTO.Response isAnalysisStartAvailable(String groupId) {

        final int submittedCountCriteria = 2;

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        Optional<Analysis> optionalAnalysis = analysisRepository.findByGroupGroupId(groupId);

        boolean isAnalysisStarted = optionalAnalysis
                .map(analysis -> !analysis.getAnalysisStatus().equals(AnalysisStatus.NOT_STARTED))
                .orElse(false);

        List<Member> memberList = memberRepository.findAllByGroupGroupId(groupId);

        int submittedCount = (int) memberList.stream()
                .filter(member -> analysisSettingRepository.existsByMemberMemberId(member.getMemberId()))
                .count();

        return IsAnalysisStartAvailableDTO.Response.builder()
                .groupId(group.getGroupId())
                .submittedCount(submittedCount)
                .isAnalysisStartConditionSatisfied(submittedCount >= submittedCountCriteria)
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

            CategorySetting categorySetting = CategorySetting.builder()
                    .analysisSetting(analysisSetting)
                    .category(category)
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
    public AnalysisStartDTO.Response analysisStart(String groupId) {

        // 그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

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

        // 비동기로 AI 분석서비스에 분석 요청
        CompletableFuture.runAsync(() -> {
            try {
                AIAnalysisDTO.Response response =
                        aiServiceAdaptor.requestAnalysis(AIAnalysisDTO.Request.builder().groupId(groupId).build());

                log.info("response => {}", response.toString());
                /**
                 *  TODO: AI 분석 성공 후 후처리
                 *  analysis.setAnalysisStatus(AnalysisStatus.COMPLETED);
                 *  analysisRepository.save(analysis);
                 *
                 */


            } catch (Exception e) {
                /**
                 * TODO: AI 분석 실패 시 처리
                 * analysis.setAnalysisStatus(AnalysisStatus.FAILED);
                 * analysisRepository.save(analysis);
                 */
            }
        }, analysisServiceExcutor);

        return AnalysisStartDTO.Response.builder()
                .groupId(group.getGroupId())
                .analysisId(analysis.getAnalysisId())
                .analysisStatus(analysis.getAnalysisStatus().toString()).build();
    }

    public String sendAiServiceApiRequest() {
        return "";
    }

}
