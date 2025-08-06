package com.waguwagu.weat.domain.category.model.dto;

import com.waguwagu.weat.domain.category.model.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDTO {

    private Long categoryId;
    private String categoryName;

    public static CategoryDTO of(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}