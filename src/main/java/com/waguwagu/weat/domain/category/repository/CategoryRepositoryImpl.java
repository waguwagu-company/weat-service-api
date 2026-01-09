package com.waguwagu.weat.domain.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.waguwagu.weat.domain.category.constant.CategoryVersion;
import com.waguwagu.weat.domain.category.model.dto.CategoryDTO;
import com.waguwagu.weat.domain.category.model.dto.QCategoryDTO;
import com.waguwagu.weat.domain.category.model.entity.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryDTO> fetchAllCategoryDtoByCategoryVersion(CategoryVersion categoryVersion) {

        QCategory c = QCategory.category;

        return queryFactory.select(
                        new QCategoryDTO(
                                c.categoryId,
                                c.categoryParent.categoryId,
                                c.categoryName,
                                c.categoryOrder,
                                c.categoryDepth,
                                c.categoryVersion
                        )
                )
                .from(c)
                .where(c.categoryVersion.eq(categoryVersion.getValue()))
                .fetch();
    }
}
