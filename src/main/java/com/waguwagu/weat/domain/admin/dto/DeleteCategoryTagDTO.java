package com.waguwagu.weat.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeleteCategoryTagDTO {

    @Getter
    @Builder
    public static class Response {
        private Long deletedCategoryTagId;
    }

}
