package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationDTO {

    @Getter
    public static class Request {
        private String input;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private boolean isValid;
        private String AIMessage;
    }
}
