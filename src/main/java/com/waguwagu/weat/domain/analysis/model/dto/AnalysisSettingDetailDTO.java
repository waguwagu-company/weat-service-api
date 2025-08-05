package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSettingDetail;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisSettingDetailDTO {

    private Long analysisSettingDetailId;
    private AnalysisSettingDTO analysisSetting;

    public static AnalysisSettingDetailDTO of(AnalysisSettingDetail entity) {
        return AnalysisSettingDetailDTO.builder()
                .analysisSettingDetailId(entity.getAnalysisSettingDetailId())
                .analysisSetting(AnalysisSettingDTO.of(entity.getAnalysisSetting()))
                .build();
    }
}