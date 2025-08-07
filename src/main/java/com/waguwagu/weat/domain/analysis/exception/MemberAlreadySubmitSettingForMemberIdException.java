package com.waguwagu.weat.domain.analysis.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class MemberAlreadySubmitSettingForMemberIdException extends BaseException {
    public MemberAlreadySubmitSettingForMemberIdException() {
        super(ErrorCode.MEMBER_ALREADY_SUBMIT_SETTING);
    }

    public MemberAlreadySubmitSettingForMemberIdException(Long memberId) {
        super(ErrorCode.MEMBER_ALREADY_SUBMIT_SETTING, ErrorCode.MEMBER_ALREADY_SUBMIT_SETTING.getMessage() + "(memberId: " + memberId + ")");
    }
}
