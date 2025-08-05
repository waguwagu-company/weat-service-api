package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSetting;
import com.waguwagu.weat.domain.group.model.dto.MemberDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisSettingDTO {

    private Long analysisSettingId;
    private AnalysisDTO analysis;
    private MemberDTO member;

    public static AnalysisSettingDTO of(AnalysisSetting entity) {
        return AnalysisSettingDTO.builder()
                .analysisSettingId(entity.getAnalysisSettingId())
                .analysis(AnalysisDTO.of(entity.getAnalysis()))
                .member(MemberDTO.of(entity.getMember()))
                .build();
    }
}