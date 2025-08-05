package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.LocationSetting;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationSettingDTO {

    private Long analysisSettingDetailId;
    private String analysisSettingDetailName;
    private AnalysisSettingDTO analysisSetting;
    private Double xPosition;
    private Double yPosition;
    private String roadnameAddress;

    public static LocationSettingDTO of(LocationSetting entity) {
        return LocationSettingDTO.builder()
                .analysisSettingDetailId(entity.getAnalysisSettingDetailId())
                .analysisSetting(AnalysisSettingDTO.of(entity.getAnalysisSetting()))
                .xPosition(entity.getXPosition())
                .yPosition(entity.getYPosition())
                .roadnameAddress(entity.getRoadnameAddress())
                .build();
    }
}