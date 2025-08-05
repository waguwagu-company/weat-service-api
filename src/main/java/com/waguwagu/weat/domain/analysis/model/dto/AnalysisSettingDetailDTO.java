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
    private AnalysisSettingDetailTypeDTO analysisSettingDetailType;
    private String analysisSettingDetailName;

    public static AnalysisSettingDetailDTO of(AnalysisSettingDetail entity) {
        return AnalysisSettingDetailDTO.builder()
                .analysisSettingDetailId(entity.getAnalysisSettingDetailId())
                .analysisSetting(AnalysisSettingDTO.of(entity.getAnalysisSetting()))
                .analysisSettingDetailType(AnalysisSettingDetailTypeDTO.of(entity.getAnalysisSettingDetailType()))
                .analysisSettingDetailName(entity.getAnalysisSettingDetailName())
                .build();
    }
}