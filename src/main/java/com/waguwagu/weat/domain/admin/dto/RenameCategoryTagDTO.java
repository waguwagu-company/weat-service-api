package com.waguwagu.weat.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RenameCategoryTagDTO {
    @Getter
    @Builder
    public static class Request{
        private Long categoryTagId;
        private String categoryTagNewName;
    }
    @Getter
    @Builder
    public static class Response {
        private Long categoryTagId;
        private String categoryTagNewName;
    }
}
