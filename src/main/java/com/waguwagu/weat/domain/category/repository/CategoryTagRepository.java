package com.waguwagu.weat.domain.category.repository;

import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryTagRepository extends JpaRepository<CategoryTag, Long> {
    List<CategoryTag> findAllByCategoryCategoryIdOrderByCategoryTagOrder(Long categoryId);
    List<CategoryTag> findByCategoryOrderByCategoryTagOrderDesc(Category category);
}
