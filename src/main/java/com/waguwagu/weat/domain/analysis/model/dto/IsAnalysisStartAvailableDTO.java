package com.waguwagu.weat.domain.analysis.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "분석 가능 여부")
public class IsAnalysisStartAvailableDTO {

    @Getter
    @Setter
    @Builder
    public static class Response {
        @Schema(description = "대상 그룹 식별자")
        private String groupId;

        @Schema(description = "제출 수")
        private int submittedCount;

        @Schema(description = "분석 시작 가능 여부", example = "true")
        @JsonProperty("isAnalysisStartConditionSatisfied")
        private Boolean isAnalysisStartConditionSatisfied;

        @Schema(description = "분석 진행 상태", example = "false")
        @JsonProperty("isAnalysisStarted")
        private Boolean isAnalysisStarted;
    }
}
