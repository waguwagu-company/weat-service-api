package com.waguwagu.weat.domain.category.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 프론트엔드 요청 사항을 반영하여 카테고리 응답은 현재 프론트 설계에 맞추어 응답
 */
@Builder
@Getter
public class GetAllCategoryListLegacyDTO {
    @Getter
    @Builder
    public static class Response {

        @Builder.Default
        private List<Category> categoryList = new ArrayList<>();

        @Getter
        @Builder
        public static class Category {
            @Schema(description = "카테고리명")
            private String title;

            @Schema(description = "카테고리 식별자")
            private Long categoryId;

            @Schema(description = "카테고리 순서")
            private Long categoryOrder;

            @Schema(description = "카테고리태그 리스트")
            @Builder.Default
            private List<Tags> tags = new ArrayList<>();

            @Getter
            @Builder
            public static class Tags {
                @Schema(description = "카테고리태그 식별자")
                private Long categoryTagId;
                @Schema(description = "카테고리태그 순서")
                private Long categoryTagOrder;

                @Schema(description = "카테고리 태그명")
                private String label;
                @Builder.Default
                private String status = "default"; // 프론트에서만 사용
            }
        }
    }
}
