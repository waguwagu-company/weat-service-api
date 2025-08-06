package com.waguwagu.weat.domain.group.exception;

import com.waguwagu.weat.domain.common.exception.EntityNotFoundException;

public class GroupNotFoundException extends EntityNotFoundException {

    private static final String DEFAULT_MESSAGE = "존재하지 않는 그룹입니다.";

    public GroupNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public GroupNotFoundException(String message) {
        super(message);
    }
}
