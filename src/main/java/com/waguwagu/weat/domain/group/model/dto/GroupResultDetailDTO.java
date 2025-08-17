package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "그룹별 분석 상세 DTO", description = "그룹별 분석 상세 DTO")
public class GroupResultDetailDTO {

    @Schema(description = "분셕결과상세 식별자")
    private Long analysisResultDetailId;

    @Schema(description = "장소 식별자")
    private Long placeId;

    @Schema(description = "장소명")
    private String placeName;

    @Schema(description = "장소 주소")
    private String placeAddress;

    @Schema(description = "장소 URL")
    private String placeUrl;

    @Schema(description = "키워드 목록")
    private List<String> keywordList;

    @Schema(description = "분석 근거 유형")
    private String analysisBasisType;

    @Schema(description = "분석 근거 내용")
    private String analysisBasisContent;

    @Schema(description = "분석 점수")
    private int analysisScore;

    @Schema(description = "장소 이미지 URL")
    private String imageUrl;


}
