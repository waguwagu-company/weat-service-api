package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.CategorySetting;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySettingDTO {

    private Long analysisSettingDetailId;
    private String analysisSettingDetailName;
    private AnalysisSettingDTO analysisSetting;
    private AnalysisSettingDetailTypeDTO analysisSettingDetailType;
    private CategoryDTO category;
    private Boolean isPreferred;

    public static CategorySettingDTO of(CategorySetting entity) {
        return CategorySettingDTO.builder()
                .analysisSettingDetailId(entity.getAnalysisSettingDetailId())
                .analysisSettingDetailName(entity.getAnalysisSettingDetailName())
                .analysisSetting(AnalysisSettingDTO.of(entity.getAnalysisSetting()))
                .analysisSettingDetailType(AnalysisSettingDetailTypeDTO.of(entity.getAnalysisSettingDetailType()))
                .category(CategoryDTO.of(entity.getCategory()))
                .isPreferred(entity.getIsPreferred())
                .build();
    }
}