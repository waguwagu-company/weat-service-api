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

    @Schema(description = "분석 결과 템플릿 메시지")
    private String analysisResultTemplateMessage;

    @Schema(description = "분석 결과 상세 내용")
    private String analysisResultContent;

    @Schema(description = "분석 근거 내용 - 리뷰 or AI 추천 멘트")
    private List<AnalysisBasisDTO> analysisBasisList;

    @Schema(description = "장소 이미지 목록")
    @Setter
    private List<PlaceImageDTO> placeImageList;


}
