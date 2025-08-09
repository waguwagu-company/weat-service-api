package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class AnalysisResultDetailNotFoundException extends BaseException {
    public AnalysisResultDetailNotFoundException() {
        super(ErrorCode.ANALYSIS_RESULT_DETAIL_NOT_FOUND);
    }

    public AnalysisResultDetailNotFoundException(Long analysisResultDetailId) {
        super(ErrorCode.ANALYSIS_RESULT_DETAIL_NOT_FOUND, ErrorCode.ANALYSIS_RESULT_DETAIL_NOT_FOUND.getMessage() + "(analysisResultDetailId: " + analysisResultDetailId + ")");
    }
}
