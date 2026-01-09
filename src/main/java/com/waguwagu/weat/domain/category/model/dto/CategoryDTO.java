package com.waguwagu.weat.domain.category.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.waguwagu.weat.domain.category.model.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    private Long categoryParentId;
    private String categoryName;
    private Integer categoryOrder;
    private Integer categoryDepth;
    private String categoryVersion;

    @QueryProjection
    public CategoryDTO(Long categoryId, Long categoryParentId, String categoryName,
                       Integer categoryOrder, Integer categoryDepth, String categoryVersion) {
        this.categoryId = categoryId;
        this.categoryParentId = categoryParentId;
        this.categoryName = categoryName;
        this.categoryOrder = categoryOrder;
        this.categoryDepth = categoryDepth;
        this.categoryVersion = categoryVersion;
    }

    public static CategoryDTO of(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }

}