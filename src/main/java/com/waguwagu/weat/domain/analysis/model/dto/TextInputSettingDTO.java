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
    private String inputText;

    public static TextInputSettingDTO of(TextInputSetting entity) {
        return TextInputSettingDTO.builder()
                .analysisSettingDetailId(entity.getAnalysisSettingDetailId())
                .analysisSetting(AnalysisSettingDTO.of(entity.getAnalysisSetting()))
                .inputText(entity.getInputText())
                .build();
    }
}