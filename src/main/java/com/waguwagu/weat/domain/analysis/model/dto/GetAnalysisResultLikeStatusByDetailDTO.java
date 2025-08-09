package com.waguwagu.weat.domain.analysis.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetAnalysisResultLikeStatusByDetailDTO {

    @Getter
    @Builder
    public static class Response {
        @Schema(description = "분석결과상세 식별자")
        private Long analysisResultDetailId;
        @Schema(description = "멤버 식별자")
        private Long memberId;
        @Schema(description = "좋아요 활성화 상태")
        private Boolean isLiked;
    }
}
