package com.waguwagu.weat.domain.analysis.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.datetime.standard.Jsr310DateTimeFormatAnnotationFormatterFactory;

@Getter
public class ToggleAnalysisResultDetailLikeDTO {

    @Getter
    public static class Request{
        @Schema(description = "분석결과상세 식별자", example = "1")
        private Long analysisResultDetailId;
        @Schema(description = "멤버 식별자", example = "2")
        private Long memberId;
    }

    @Getter
    @Builder
    public static class Response{
        @Schema(description = "분석결과상세 식별자", example = "1")
        private Long analysisResultDetailId;
        @Schema(description = "멤버 식별자", example = "2")
        private Long memberId;
        @Schema(description = "좋아요 활성화 상태", example = "true")
        private Boolean isLiked;
    }
}
