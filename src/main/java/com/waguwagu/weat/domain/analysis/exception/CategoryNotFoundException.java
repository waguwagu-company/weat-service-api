package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.domain.common.exception.EntityNotFoundException;

public class CategoryNotFoundException extends EntityNotFoundException{

    private static final String DEFAULT_MESSAGE = "존재하지 않는 카테고리입니다.";

    public CategoryNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
    
}
