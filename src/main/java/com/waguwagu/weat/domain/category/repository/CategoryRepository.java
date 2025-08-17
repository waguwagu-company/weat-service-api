package com.waguwagu.weat.domain.category.repository;

import com.waguwagu.weat.domain.category.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT public.get_category_tree()", nativeQuery = true)
    String getAllCategoryList();
}
