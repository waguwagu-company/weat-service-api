package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
public class AIAnalysisDTO {

    @Data
    @Builder
    public static class Request {
        // TODO: 임시 테스트 용 요청
        private String groupId;
    }

    @Data
    public static class Response {
        // TODO: 임시 테스트 용 응답
        private String groupId;
        private String result;
    }
}
