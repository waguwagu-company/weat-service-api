package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class AnalysisNotFoundForGroupIdException extends BaseException {
    public AnalysisNotFoundForGroupIdException() {
        super(ErrorCode.ANALYSIS_NOT_FOUND);
    }

    public AnalysisNotFoundForGroupIdException(String groupId) {
        super(ErrorCode.ANALYSIS_NOT_FOUND, ErrorCode.ANALYSIS_NOT_FOUND.getMessage() + "(groupId: " + groupId + ")");
    }
}
