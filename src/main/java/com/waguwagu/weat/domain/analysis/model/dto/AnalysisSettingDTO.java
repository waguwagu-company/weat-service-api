package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSetting;
import com.waguwagu.weat.domain.group.model.entity.Member;
import lombok.*;

@Builder
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisSettingDTO {

    private Long analysisSettingId;
    private Analysis analysis;
    private Member member;

    public static AnalysisSettingDTO of(AnalysisSetting entity) {
        return AnalysisSettingDTO.builder()
                .analysisSettingId(entity.getAnalysisSettingId())
                .analysis(entity.getAnalysis())
                .member(entity.getMember())
                .build();
    }
}