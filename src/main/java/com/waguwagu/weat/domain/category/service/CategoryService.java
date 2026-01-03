package com.waguwagu.weat.domain.category.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListDTO;
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
