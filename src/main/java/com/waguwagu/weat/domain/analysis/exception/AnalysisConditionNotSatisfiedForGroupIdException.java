package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class AnalysisConditionNotSatisfiedForGroupIdException extends BaseException {
    public AnalysisConditionNotSatisfiedForGroupIdException() {
        super(ErrorCode.ANALYSIS_CONDITION_NOT_SATISFIED);
    }

    public AnalysisConditionNotSatisfiedForGroupIdException(String groupId) {
        super(ErrorCode.ANALYSIS_CONDITION_NOT_SATISFIED, ErrorCode.ANALYSIS_CONDITION_NOT_SATISFIED.getMessage() + "(groupId: " + groupId + ")");
    }
}
