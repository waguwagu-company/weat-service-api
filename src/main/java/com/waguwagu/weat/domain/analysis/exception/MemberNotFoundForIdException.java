package com.waguwagu.weat.domain.analysis.exception;

public class MemberNotFoundForIdException extends MemberNotFoundException{
    private static final String DEFAULT_MESSAGE = "존재하지 않는 멤버입니다.";

    public MemberNotFoundForIdException(Long memberId) {
        super(DEFAULT_MESSAGE + "(memberId: " + memberId + ")");
    }

    public MemberNotFoundForIdException() {
        super(DEFAULT_MESSAGE);
    }

}
