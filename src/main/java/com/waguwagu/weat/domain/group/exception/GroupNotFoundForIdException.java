package com.waguwagu.weat.domain.group.exception;

public class GroupNotFoundForIdException extends GroupNotFoundException {
    private static final String DEFAULT_MESSAGE = "존재하지 않는 그룹입니다.";

    public GroupNotFoundForIdException(String groupId) {
        super(DEFAULT_MESSAGE + "(groupId: " + groupId + ")");
    }

    public GroupNotFoundForIdException() {
        super(DEFAULT_MESSAGE);
    }

}
