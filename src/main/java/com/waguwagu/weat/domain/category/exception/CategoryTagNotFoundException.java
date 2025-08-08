package com.waguwagu.weat.domain.category.exception;

import com.waguwagu.weat.global.exception.BaseException;
import com.waguwagu.weat.global.exception.ErrorCode;

public class CategoryTagNotFoundException extends BaseException {

    public CategoryTagNotFoundException() {
        super(ErrorCode.CATEGORY_TAG_NOT_FOUND);
    }

    public CategoryTagNotFoundException(Long categoryTagId) {
        super(ErrorCode.CATEGORY_TAG_NOT_FOUND, ErrorCode.CATEGORY_TAG_NOT_FOUND.getMessage() + " (categoryTagId: " + categoryTagId + ")");
    }

}
