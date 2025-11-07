package com.waguwagu.weat.domain.group.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GroupAnalysisBasisQueryDTO {
    private Integer analysisResultDetailId;
    private List<String> analysisResultKeywords;
    private Integer placeId;
    private String placeName;
    private String placeRoadnameAddress;
    private String placeUrl;
    private int analysisScore;
    private String analysisBasisType;
    private String analysisBasisContent;
    private String placeImageUrl;


    @QueryProjection
    public GroupAnalysisBasisQueryDTO(
            Integer analysisResultDetailId,
            List<String> analysisResultKeywords,
            Integer placeId,
            String placeName,
            String placeRoadnameAddress,
            String placeUrl,
            int analysisScore,
            String analysisBasisType,
            String analysisBasisContent,
            String placeImageUrl
    ) {
        this.analysisResultDetailId = analysisResultDetailId;
        this.analysisResultKeywords = analysisResultKeywords;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeRoadnameAddress = placeRoadnameAddress;
        this.placeUrl = placeUrl;
        this.analysisScore = analysisScore;
        this.analysisBasisType = analysisBasisType;
        this.analysisBasisContent = analysisBasisContent;
        this.placeImageUrl = placeImageUrl;
    }
}

