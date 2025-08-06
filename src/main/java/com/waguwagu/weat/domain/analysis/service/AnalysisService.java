package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundException;
import com.waguwagu.weat.domain.analysis.model.dto.IsAnalysisStartAvailableDTO;
import com.waguwagu.weat.domain.analysis.model.dto.IsMemberSubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.model.dto.SubmitAnalysisSettingDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;


    // 분석 시작가능조건 충족여부 및 분석상태 조회
    public IsAnalysisStartAvailableDTO.Response isAnalysisStartAvailable(String groupId) {

        // TODO: 추후 명확한 기준(제출 인원)이 정해지면 수정 예정
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
}
