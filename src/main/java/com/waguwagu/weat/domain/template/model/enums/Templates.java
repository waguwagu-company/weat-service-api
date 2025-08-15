package com.waguwagu.weat.domain.template.model.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum Templates {

    REVIEW_RESULT_INTRO("REVIEW_RESULT_INTRO", List.of("keywords")),
    AI_RESULT_INTRO("AI_RESULT_INTRO", List.of("keywords"));

    private final String title;
    private final List<String> placeholders;

    Templates(String title, List<String> placeholders) {
        this.title = title;
        this.placeholders = placeholders;
    }
}
