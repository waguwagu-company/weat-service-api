package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class LocationSettingNotFoundForMemberIdException extends BaseException {
    public LocationSettingNotFoundForMemberIdException() {
        super(ErrorCode.LOCATION_SETTING_NOT_FOUND);
    }

    public LocationSettingNotFoundForMemberIdException(Long memberId) {
        super(ErrorCode.LOCATION_SETTING_NOT_FOUND, ErrorCode.LOCATION_SETTING_NOT_FOUND.getMessage() + "(memberId: " + memberId + ")");
    }
}
