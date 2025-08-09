package com.waguwagu.weat.domain.analysis.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class GetAnalysisResultLikeCountDTO {

    @Getter
    @Builder
    public static class Response {
        @Schema(description = "분석결과상세 식별자", example = "1")
        private Long analysisResultDetailId;

        @Schema(description = "좋아요 수", example = "3")
        private Long likeCount;
    }
}
