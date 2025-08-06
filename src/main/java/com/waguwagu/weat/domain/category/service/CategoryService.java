package com.waguwagu.weat.domain.category.service;


import com.waguwagu.weat.domain.category.model.dto.CategoryDTO;
import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListDTO;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public GetAllCategoryListDTO.Response getAllCategoryList() {
        List<Category> categorieList = categoryRepository.findAll();

        GetAllCategoryListDTO.Response response = new GetAllCategoryListDTO.Response();

        for (Category category : categorieList) {
            response.getCategoryList().add(GetAllCategoryListDTO.Response.Category.builder()
                    .categoryId(category.getCategoryId())
                    .categoryName(category.getCategoryName())
                    .build());
        }

        return response;
    }
}
