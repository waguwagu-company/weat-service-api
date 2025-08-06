package com.waguwagu.weat.domain.group.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class GroupMemberLimitExceededException extends BaseException {

    public GroupMemberLimitExceededException() {
        super(ErrorCode.MEMBER_LIMIT_EXCEEDED);
    }
}

