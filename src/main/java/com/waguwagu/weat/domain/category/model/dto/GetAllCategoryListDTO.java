package com.waguwagu.weat.domain.category.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAllCategoryListDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        @Builder.Default
        @Schema(description = "카테고리 리스트")
        private List<Category> categoryList = new ArrayList<>();

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Category {
            @Schema(description = "카테고리명")
            private String title;
            @Schema(description = "카테고리 식별자")
            private int categoryId;
            @Schema(description = "카테고리 순서")
            private int categoryOrder;
            @Builder.Default
            @Schema(description = "카테고리 태그 리스트")
            private List<CategoryTag> tags = new ArrayList<>();
            @Builder.Default
            @Schema(description = "자식 카테고리 리스트")
            private List<Category> children = new ArrayList<>();
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CategoryTag {
            @Schema(description = "카테고리 태그 식별자")
            private int categoryTagId;
            @Schema(description = "카테고리 태그 순서")
            private int categoryTagOrder;
            @Schema(description = "카테고리 태그명")
            private String label;
            @Schema(description = "카테고리 태그 상태")
            private String status;
        }
    }
}
