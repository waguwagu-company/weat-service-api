package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(name = "분석 결과 쿼리 DTO", description = "분석 결과 쿼리 DTO")
public class GroupResultQueryDTO {

    private String analysisResultDetailContent;  // 분석 결과 상세 내용
    private String analysisBasisType;            // 분석 기준 타입
    private String analysisBasisContent;         // 분석 기준 내용
    private Long placeId;                        // 장소 ID
    private String placeName;                    // 장소 이름
    private String placeRoadnameAddress;         // 도로명 주소
    private String placeImageUrl;                // 이미지 URL (이미지 1개 기준)
}
