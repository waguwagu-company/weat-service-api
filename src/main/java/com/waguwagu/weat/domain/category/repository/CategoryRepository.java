package com.waguwagu.weat.domain.category.repository;

import com.waguwagu.weat.domain.category.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
