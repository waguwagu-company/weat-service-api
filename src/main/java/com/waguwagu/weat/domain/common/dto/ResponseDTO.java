package com.waguwagu.weat.domain.common.dto;

import com.waguwagu.weat.global.exception.ErrorCode;
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

    @Schema(description = "응답 코드", example = "GROUP_NOT_FOUND")
    private String code;
    
    @Schema(description = "응답 메시지", example = "SUCCESS")
    private String message;
    
    @Schema(description = "응답 데이터")
    private T data;

    public ResponseDTO<T> code(String code) {
        this.code = code;
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
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data);
    }

    public static <T> ResponseDTO<T> fail(String code, String message) {
        return new ResponseDTO<T>()
                .code(code)
                .message(message)
                .data(null);
    }
}