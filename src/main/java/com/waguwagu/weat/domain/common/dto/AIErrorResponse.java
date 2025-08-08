package com.waguwagu.weat.domain.common.dto;

import lombok.Getter;

@Getter
public class AIErrorResponse {
    private boolean success;
    private String code;
    private String message;
}