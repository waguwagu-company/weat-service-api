package com.waguwagu.weat.global.exception;

import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDTO<?>> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getStatus())
                .body(ResponseDTO.fail(e.getErrorCode(), e.getMessage()));
    }

    // TODO: 그 외 비즈니스 로직 아닌 에러 중 분기처리 필요한 건 추가 (SQLException)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseDTO<?>> handleUnhandledException(Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ResponseDTO.fail("INTERNAL_SERVER_ERROR", "알 수 없는 서버 오류입니다."));
//    }

}
