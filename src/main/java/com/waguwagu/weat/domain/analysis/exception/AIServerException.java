package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class AIServerException extends BaseException {
    public AIServerException() {
        super(ErrorCode.AI_SERVER_ERROR);
    }

    public AIServerException(String aiServerMessage) {
        super(ErrorCode.AI_SERVER_ERROR, ErrorCode.AI_SERVER_ERROR.getMessage() + aiServerMessage);
    }
    
}
