package com.waguwagu.weat.domain.analysis.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "분석 시작 DTO")
public class AnalysisStartDTO {

    @Getter
    @Schema(description = "분석 시작 요청")
    public static class Request {
        @Schema(description = "그룹 식별자", example = "f5d8931a830e41968663ce0dc12bf9b2")
        private String groupId;

        @Schema(description = "개인으로 참여 여부", example = "false")
        private Boolean isIndividualAnalysis;
    }

    @Getter
    @Builder
    @Schema(description = "분석 시작 응답")
    public static class Response {
        @Schema(description = "그룹 식별자", example = "f5d8931a830e41968663ce0dc12bf9b2")
        private String groupId;
        @Schema(description = "분석 식별자", example = "2")
        private Long analysisId;
        @Schema(description = "분석 진행 상태", example = "IN_PROGRESS")
        private String analysisStatus;
    }
}
