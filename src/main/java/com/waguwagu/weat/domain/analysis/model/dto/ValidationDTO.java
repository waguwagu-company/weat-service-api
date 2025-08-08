package com.waguwagu.weat.domain.analysis.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Schema(description = "사용자 입력 유효성 검사")
public class ValidationDTO {

    @Getter
    public static class Request {
        @Schema(description = "사용자 입력값")
        private String userInput;
    }

    @Getter
    public static class Response {
        @JsonProperty("isValid")
        @Schema(description = "유효 여부")
        private Boolean isValid;

        @Schema(description = "AI 메세지")
        private String message;
    }
}
