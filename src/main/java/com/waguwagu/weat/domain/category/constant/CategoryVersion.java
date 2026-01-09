package com.waguwagu.weat.domain.category.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryVersion {
    V1("v1"),
    V2("v2"),
    ;

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
