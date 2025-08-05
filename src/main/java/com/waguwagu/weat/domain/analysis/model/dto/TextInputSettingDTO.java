package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.TextInputSetting;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextInputSettingDTO {

    private Long analysisSettingDetailId;
    private String analysisSettingDetailName;
    private AnalysisSettingDTO analysisSetting;
    private AnalysisSettingDetailTypeDTO analysisSettingDetailType;
    private String inputText;

    public static TextInputSettingDTO of(TextInputSetting entity) {
        return TextInputSettingDTO.builder()
                .analysisSettingDetailId(entity.getAnalysisSettingDetailId())
                .analysisSettingDetailName(entity.getAnalysisSettingDetailName())
                .analysisSetting(AnalysisSettingDTO.of(entity.getAnalysisSetting()))
                .analysisSettingDetailType(AnalysisSettingDetailTypeDTO.of(entity.getAnalysisSettingDetailType()))
                .inputText(entity.getInputText())
                .build();
    }
}