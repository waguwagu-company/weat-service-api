package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class AnalysisAlreadyStartedForGroupIdException extends BaseException {

    public AnalysisAlreadyStartedForGroupIdException() {
        super(ErrorCode.ANALYSIS_ALREADY_STARTED);
    }

    public AnalysisAlreadyStartedForGroupIdException(String groupId) {
        super(ErrorCode.ANALYSIS_ALREADY_STARTED, ErrorCode.ANALYSIS_ALREADY_STARTED.getMessage() + "(groupId: " + groupId + ")");
    }

}
