package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSettingDetailType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalysisSettingDetailTypeDTO {

    private Long analysisSettingDetailTypeId;
    private String analysisSettingDetailName;

    public static AnalysisSettingDetailTypeDTO of(AnalysisSettingDetailType entity) {
        return AnalysisSettingDetailTypeDTO.builder()
                .analysisSettingDetailTypeId(entity.getAnalysisSettingDetailTypeId())
                .analysisSettingDetailName(entity.getAnalysisSettingDetailName())
                .build();
    }
}