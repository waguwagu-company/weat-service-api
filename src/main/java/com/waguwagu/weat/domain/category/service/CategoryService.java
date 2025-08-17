package com.waguwagu.weat.domain.category.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListDTO;
import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListLegacyDTO;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryTagRepository categoryTagRepository;
    private final ObjectMapper objectMapper;

    public GetAllCategoryListLegacyDTO.Response getAllCategoryListLegacy() {
        List<Category> categorieList = categoryRepository.findAll();

        List<GetAllCategoryListLegacyDTO.Response.Category> responseCategoryList = new ArrayList<>();

        for (Category category : categorieList) {
            List<CategoryTag> categoryTagList =
                    categoryTagRepository.findAllByCategoryCategoryIdOrderByCategoryTagOrder(category.getCategoryId());

            List<GetAllCategoryListLegacyDTO.Response.Category.Tags> responseTagList = new ArrayList<>();

            for (CategoryTag categoryTag : categoryTagList) {
                responseTagList.add(GetAllCategoryListLegacyDTO.Response.Category.Tags.builder()
                        .categoryTagId(categoryTag.getCategoryTagId())
                        .label(categoryTag.getCategoryTagName())
                        .categoryTagOrder(categoryTag.getCategoryTagOrder())
                        .build());
            }

            GetAllCategoryListLegacyDTO.Response.Category responseCategory =
                    GetAllCategoryListLegacyDTO.Response.Category.builder()
                            .categoryId(category.getCategoryId())
                            .title(category.getCategoryName())
                            .categoryOrder(category.getCategoryOrder())
                            .tags(responseTagList)
                            .build();
            responseCategoryList.add(responseCategory);
        }

        return GetAllCategoryListLegacyDTO.Response.builder()
                .categoryList(responseCategoryList)
                .build();
    }


    public GetAllCategoryListDTO.Response getAllCategoryList(){
        String json = categoryRepository.getAllCategoryList();
        try {
            return objectMapper.readValue(json, GetAllCategoryListDTO.Response.class);
        } catch (Exception e) {

            log.error("카테고리 트리 조회 실패 => {}", e.getMessage());

            return GetAllCategoryListDTO.Response.builder()
                    .categoryList(new ArrayList<>())
                    .build();
        }
    }
}
