package com.waguwagu.weat.domain.category.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetAllCategoryListDTO {
    @Getter
    public static class Response {

        @Schema(description = "카테고리 리스트")
        private List<Category> categoryList = new ArrayList<>();

        @Getter
        @Builder
        public static class Category {
            @Schema(description = "카테고리 식별자")
            private Long categoryId;
            @Schema(description = "카테고리명")
            private String categoryName;
        }
    }
}
