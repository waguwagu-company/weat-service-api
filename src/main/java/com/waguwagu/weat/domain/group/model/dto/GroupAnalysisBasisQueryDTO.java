package com.waguwagu.weat.domain.group.model.dto;

import lombok.Getter;

@Getter
public class GroupAnalysisBasisQueryDTO {
    private Long placeId;
    private String placeName;
    private String placeRoadnameAddress;
    private String analysisResultDetailContent;
    private String analysisBasisType;
    private String analysisBasisContent;
}

