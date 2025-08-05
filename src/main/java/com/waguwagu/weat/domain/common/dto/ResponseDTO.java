package com.waguwagu.weat.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 응답 DTO")
public class ResponseDTO<T> {

    @JsonIgnore
    private static final int DEFAULT_STATUS = HttpStatus.OK.value();
    @JsonIgnore
    private static final String DEFAULT_MESSAGE = "SUCCESS";

    @Schema(description = "응답 상태 코드", example = "200")
    private int status = DEFAULT_STATUS;
    
    @Schema(description = "응답 메시지", example = "SUCCESS")
    private String message = DEFAULT_MESSAGE;
    
    @Schema(description = "응답 데이터")
    private T data;

    public ResponseDTO<T> status(int status) {
        this.status = status;
        return this;
    }

    public ResponseDTO<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResponseDTO<T> data(T data) {
        this.data = data;
        return this;
    }

    public static <T> ResponseDTO<T> of(T data) {
        return new ResponseDTO<T>()
                .status(DEFAULT_STATUS)
                .message(DEFAULT_MESSAGE)
                .data(data);
    }
}