package com.waguwagu.weat.global.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AIErrorResponse {
    private boolean success;
    private String code;
    private String message;
}