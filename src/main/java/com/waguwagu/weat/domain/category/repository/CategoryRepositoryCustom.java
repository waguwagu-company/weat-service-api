package com.waguwagu.weat.domain.category.repository;

import com.waguwagu.weat.domain.category.constant.CategoryVersion;
import com.waguwagu.weat.domain.category.model.dto.CategoryDTO;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<CategoryDTO> fetchAllCategoryDtoByCategoryVersion(CategoryVersion categoryVersion);
}
