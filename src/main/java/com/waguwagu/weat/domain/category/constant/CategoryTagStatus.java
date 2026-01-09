package com.waguwagu.weat.domain.category.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryTagStatus {
    DEFAULT("default"),
    ;

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
