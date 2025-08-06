package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResultDetailDTO {

    @Schema(description = "장소 식별자")
    private Long placeId;

    @Schema(description = "장소명")
    private String placeName;

    @Schema(description = "장소 주소")
    private String placeAddress;

    @Schema(description = "분석 결과 상세 내용")
    private String analysisResultContent;

    @Schema(description = "분석 근거 내용 - 리뷰 or AI 추천 멘트")
    private String analysisBasisContent;

    @Schema(description = "장소 이미지 목록")
    @Setter
    List<PlaceImageDTO> placeImageList;


}
