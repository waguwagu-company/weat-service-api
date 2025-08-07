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
        @Schema(description = "대상 그룹 식별자", example = "f5d8931a830e41968663ce0dc12bf9b2")
        private String groupId;

        @Schema(description = "그룹 내 설정을 제출한 멤버 수", example = "2")
        private int submittedCount;

        @Schema(description = "분석 시작 가능 여부", example = "true")
        @JsonProperty("isAnalysisStartConditionSatisfied")
        private Boolean isAnalysisStartConditionSatisfied;

        // TODO: 추후 분석 진행상태로 변경 예정
        @Schema(description = "분석 시작 여부", example = "false")
        @JsonProperty("isAnalysisStarted")
        private Boolean isAnalysisStarted;
    }
}
