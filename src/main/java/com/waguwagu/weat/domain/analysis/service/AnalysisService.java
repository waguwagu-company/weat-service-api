package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.model.dto.CategorySettingDTO;
import com.waguwagu.weat.domain.analysis.model.dto.SubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSetting;
import com.waguwagu.weat.domain.analysis.model.entity.Category;
import com.waguwagu.weat.domain.analysis.model.entity.CategorySetting;
import com.waguwagu.weat.domain.analysis.model.entity.LocationSetting;
import com.waguwagu.weat.domain.analysis.model.entity.TextInputSetting;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingDetailRepository;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingRepository;
import com.waguwagu.weat.domain.analysis.repository.CategoryRepository;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.waguwagu.weat.domain.analysis.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;

    @Transactional
    // 개인별 분석 설정 제출
    public SubmitAnalysisSettingDTO.Response submitAnalysisSetting(SubmitAnalysisSettingDTO.Request requestDto) {
        // 회원 정보 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(RuntimeException::new);

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
        for (CategorySettingDTO categorySettingDTO : requestDto.getCategorySettingList()) {
            Category category = categoryRepository.findById(categorySettingDTO.getCategory().getCategoryId())
                    .orElseThrow(RuntimeException::new);
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
                .inputText(requestDto.getTextInputSettingDTO().getInputText())
                .build();
        analysisSettingDetailRepository.save(textInputSetting);

        return SubmitAnalysisSettingDTO.Response.builder()
                .memberId(member.getMemberId())
                .settingId(analysisSetting.getAnalysisSettingId())
                .build();
    }
}
