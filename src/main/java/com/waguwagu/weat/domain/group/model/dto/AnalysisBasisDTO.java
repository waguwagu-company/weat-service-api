package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "분석 근거 DTO", description = "분석 근거 DTO")
public class AnalysisBasisDTO {

    @Schema(description = "분석 근거 유형")
    private String analysisBasisType;

    @Schema(description = "분석 근거 내용")
    private String analysisBasisContent;
}
