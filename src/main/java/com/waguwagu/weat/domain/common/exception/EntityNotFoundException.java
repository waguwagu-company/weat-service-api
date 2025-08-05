package com.waguwagu.weat.domain.common.exception;

public class EntityNotFoundException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "존재하지 않는 엔티티입니다.";
    
    public EntityNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
    
}
