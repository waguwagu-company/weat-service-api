package com.waguwagu.weat.domain.analysis.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "분석 상태 조회")
public class GetAnalysisStatusDTO {

    @Getter
    @Setter
    @Builder
    public static class Response {
        @Schema(description = "대상 그룹 식별자", example = "f5d8931a830e41968663ce0dc12bf9b2")
        private String groupId;

        @Schema(description = "1인 그룹 여부", example = "false")
        @JsonProperty("isSingleMemberGroup")
        private Boolean isSingleMemberGroup;

        @Schema(description = "그룹 내 설정을 제출한 멤버 수", example = "2")
        private int submittedCount;

        @Schema(description = "분석시작조건 충족 여부", example = "true")
        @JsonProperty("isAnalysisStartConditionSatisfied")
        private Boolean isAnalysisStartConditionSatisfied;

        @Schema(description = "분석 진행 상태", example = "NOT_STARTED")
        @JsonProperty("analysisStatus")
        private String analysisStatus;
    }
}
