package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationDTO {

    @Getter
    public static class Request {
        private String input;
    }

    @Getter
    @Builder
    public static class Response {

        private boolean isValid;
        private String AIMessage;
    }
}
