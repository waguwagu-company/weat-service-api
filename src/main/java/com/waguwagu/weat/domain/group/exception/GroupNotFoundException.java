package com.waguwagu.weat.domain.group.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class GroupNotFoundException extends BaseException {

    public GroupNotFoundException() {
        super(ErrorCode.GROUP_NOT_FOUND);
    }

    public GroupNotFoundException(String groupId) {
        super(ErrorCode.GROUP_NOT_FOUND, ErrorCode.GROUP_NOT_FOUND.getMessage() + " (groupId: " + groupId + ")");
    }
}

