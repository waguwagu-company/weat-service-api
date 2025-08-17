package com.waguwagu.weat.domain.group.model.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupAnalysisBasisQueryDTO {
    private Long analysisResultDetailId;
    private String analysisResultKeywords;
    private Long placeId;
    private String placeName;
    private String placeRoadnameAddress;
    //private String analysisResultDetailTemplateMessage;
    //private String analysisResultDetailContent;
    private int analysisScore;
    private String analysisBasisType;
    private String analysisBasisContent;
    private String placeImageUrl;
}

