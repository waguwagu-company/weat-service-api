package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AnalysisStartDTO {

    @Getter
    public static class Request {
        private String groupId;
    }

    @Getter
    @Builder
    public static class Response {
        private String groupId;
        private Long analysisId;
        private String analysisStatus;
    }
}
