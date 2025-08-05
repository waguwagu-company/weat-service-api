package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.domain.common.exception.EntityNotFoundException;

public class MemberNotFoundException extends EntityNotFoundException {
    private static final String DEFAULT_MESSAGE = "존재하지 않는 멤버입니다.";

    public MemberNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
    
    public MemberNotFoundException(String message) {
        super(message);
    }
    
}
