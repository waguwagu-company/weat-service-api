package com.waguwagu.weat.domain.category.repository;

import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryTagRepository extends JpaRepository<CategoryTag, Long> {
    List<CategoryTag> findByCategoryOrderByCategoryTagOrderDesc(Category category);

    @EntityGraph(attributePaths = "category")
    List<CategoryTag> findAllByCategory_CategoryIdInOrderByCategoryTagOrder(List<Long> categoryIds);
}
