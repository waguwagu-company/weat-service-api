package com.waguwagu.weat.domain.analysis.exception;

public class CategoryNotFoundForIdException extends CategoryNotFoundException{
    private static final String DEFAULT_MESSAGE = "존재하지 않는 카테고리입니다.";

    public CategoryNotFoundForIdException(Long categoryId) {
        super(DEFAULT_MESSAGE + "(categoryId: " + categoryId + ")");
    }

    public CategoryNotFoundForIdException() {
        super(DEFAULT_MESSAGE);
    }
    
}
