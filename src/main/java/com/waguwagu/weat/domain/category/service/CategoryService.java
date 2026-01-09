package com.waguwagu.weat.domain.category.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waguwagu.weat.domain.category.constant.CategoryTagStatus;
import com.waguwagu.weat.domain.category.constant.CategoryVersion;
import com.waguwagu.weat.domain.category.model.dto.CategoryDTO;
import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListDTO;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    /**
     * 카테고리 리스트를 트리구조로 조회
     *
     * @apiNote 카테고리 v1 버전이 더이상 사용되지 않음에 따라 기본적으로 v2 버전만 조회한다. (v1 버전의 카테고리는 추후 제거 예정)
     */
    public GetAllCategoryListDTO.Response getAllCategories() {

        // 계층구조 없는 카테고리 리스트 조회
        List<CategoryDTO> flatCategories = categoryRepository.fetchAllCategoryDtoByCategoryVersion(CategoryVersion.V2);

        // 조회된 카테고리가 없는 경우 빈 리스트 반환
        if (flatCategories.isEmpty()) {
            return GetAllCategoryListDTO.Response.builder()
                    .categoryList(List.of())
                    .build();
        }

        // 조회된 카테고리 식별자에 해당하는 카테고리 태그 조회
        List<CategoryTag> categoryTags = categoryTagRepository.findAllByCategory_CategoryIdInOrderByCategoryTagOrder(
                flatCategories.stream().map(CategoryDTO::getCategoryId).toList()
        );

        // 카테고리 식별자별 태그 리스트 맵
        Map<Long, List<GetAllCategoryListDTO.Response.CategoryTag>> tagsByCategoryIdMap = categoryTags.stream()
                .collect(Collectors.groupingBy(
                                ct -> ct.getCategory().getCategoryId(),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator
                                                        .comparing(CategoryTag::getCategoryTagOrder)
                                                        .thenComparing(CategoryTag::getCategoryTagId))
                                                .map(t -> GetAllCategoryListDTO.Response.CategoryTag.builder()
                                                        .categoryTagId(t.getCategoryTagId())
                                                        .categoryTagOrder(t.getCategoryTagOrder())
                                                        .label(t.getCategoryTagName())
                                                        .status(CategoryTagStatus.DEFAULT)
                                                        .build())
                                                .toList()
                                )
                        )
                );

        // 카테고리 식별자별 응답 카테고리 맵
        Map<Long, GetAllCategoryListDTO.Response.Category> resultCategoryMap = flatCategories.stream()
                .map(dto ->
                        GetAllCategoryListDTO.Response.Category.builder()
                                .title(dto.getCategoryName())
                                .categoryId(dto.getCategoryId())
                                .categoryOrder(dto.getCategoryOrder())
                                .tags(tagsByCategoryIdMap.getOrDefault(dto.getCategoryId(), List.of()))
                                .children(new ArrayList<>())
                                .build()
                ).collect(Collectors.toMap(
                                GetAllCategoryListDTO.Response.Category::getCategoryId,
                                Function.identity()
                        )
                );

        // 루트 카테고리 여부 판단 조건
        Predicate<CategoryDTO> isRoot = dto -> dto.getCategoryParentId() == null;
        Predicate<CategoryDTO> isNotRoot = isRoot.negate();

        // 루트 카테고리인 경우 트리에 추가
        List<GetAllCategoryListDTO.Response.Category> resultCategoryList = flatCategories.stream()
                .filter(isRoot)
                .map(dto -> resultCategoryMap.get(dto.getCategoryId()))
                .toList();

        // 루트 카테고리가 아닌 경우, 부모를 찾아 부모의 자식으로 추가
        flatCategories.stream()
                .filter(isNotRoot)
                .forEach(dto -> {
                            GetAllCategoryListDTO.Response.Category parent = resultCategoryMap.get(dto.getCategoryParentId());
                            GetAllCategoryListDTO.Response.Category child = resultCategoryMap.get(dto.getCategoryId());

                            if (parent == null) {
                                log.warn("카테고리(childId={})의 부모 카테고리(parentId={}) 데이터가 존재하지 않습니다.", dto.getCategoryId(), dto.getCategoryParentId());
                                return;
                            }

                            if (child == null) {
                                log.warn("카테고리(childId={}) 데이터가 존재하지 않습니다.", dto.getCategoryId());
                                return;
                            }

                            parent.getChildren().add(child);
                        }
                );

        return GetAllCategoryListDTO.Response.builder()
                .categoryList(resultCategoryList)
                .build();
    }
}
