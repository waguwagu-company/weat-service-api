package com.waguwagu.weat.domain.category.service;

import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListDTO;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryTagRepository categoryTagRepository;

    public GetAllCategoryListDTO.Response getAllCategoryList() {
        List<Category> categorieList = categoryRepository.findAll();

        List<GetAllCategoryListDTO.Response.Category> responseCategoryList = new ArrayList<>();

        for (Category category : categorieList) {
            List<CategoryTag> categoryTagList =
                    categoryTagRepository.findAllByCategoryCategoryIdOrderByCategoryTagOrder(category.getCategoryId());

            List<GetAllCategoryListDTO.Response.Category.Tags> responseTagList = new ArrayList<>();

            for (CategoryTag categoryTag : categoryTagList) {
                responseTagList.add(GetAllCategoryListDTO.Response.Category.Tags.builder()
                        .categoryTagId(categoryTag.getCategoryTagId())
                        .label(categoryTag.getCategoryTagName())
                        .categoryTagOrder(categoryTag.getCategoryTagOrder())
                        .build());
            }

            GetAllCategoryListDTO.Response.Category responseCategory =
                    GetAllCategoryListDTO.Response.Category.builder()
                            .categoryId(category.getCategoryId())
                            .title(category.getCategoryName())
                            .categoryOrder(category.getCategoryOrder())
                            .tags(responseTagList)
                            .build();
            responseCategoryList.add(responseCategory);
        }

        return GetAllCategoryListDTO.Response.builder()
                .categoryList(responseCategoryList)
                .build();
    }
}
