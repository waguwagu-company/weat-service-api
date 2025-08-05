package com.waguwagu.weat.domain.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundException;
import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundForIdException;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import com.waguwagu.weat.domain.common.exception.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleEntityNotFoundException(EntityNotFoundException e) {
        ResponseDTO<Void> response = new ResponseDTO<Void>().status(HttpStatus.NOT_FOUND.value()).message(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMemberNotFoundException(MemberNotFoundException e) {
        ResponseDTO<Void> response = new ResponseDTO<Void>().status(HttpStatus.NOT_FOUND.value()).message(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MemberNotFoundForIdException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMemberNotFoundForIdException(MemberNotFoundForIdException e) {
        ResponseDTO<Void> response = new ResponseDTO<Void>().status(HttpStatus.NOT_FOUND.value()).message(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
