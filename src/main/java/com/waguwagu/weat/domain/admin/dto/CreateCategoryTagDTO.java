package com.waguwagu.weat.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCategoryTagDTO {

    @Getter
    public static class Request {
        private String categoryTagName;
    }

    @Getter
    @Builder
    public static class Response {
        private Long categoryTagId;
        private String categoryTagName;
        private Long categoryTagOrder;
    }
}
