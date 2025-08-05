package com.waguwagu.weat.domain.analysis.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Schema(description = "멤버별 분석설정 제출 여부 조회")
public class IsMemberSubmitAnalysisSettingDTO {

    @Data
    @Builder
    public static class Response {
        @Schema(description = "멤버 식별자")
        private Long memberId;

        @JsonProperty("isSubmitted")
        @Schema(description = "분석설정 제출 여부")
        private boolean isSubmitted;
    }
}
