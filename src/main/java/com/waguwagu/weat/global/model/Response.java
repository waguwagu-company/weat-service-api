package com.waguwagu.weat.global.model;

import com.waguwagu.weat.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 응답 DTO")
public class Response<T> {

    @Schema(description = "응답 코드", example = "SUCCESS")
    private String code;
    
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;
    
    @Schema(description = "응답 데이터")
    private T data;

    public Response<T> code(String code) {
        this.code = code;
        return this;
    }

    public Response<T> message(String message) {
        this.message = message;
        return this;
    }

    public Response<T> data(T data) {
        this.data = data;
        return this;
    }

    public static <T> Response<T> of(T data) {
        return new Response<T>()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data);
    }

    public static <T> Response<T> fail(String code, String message) {
        return new Response<T>()
                .code(code)
                .message(message)
                .data(null);
    }
}