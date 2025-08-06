package com.waguwagu.weat.domain.category.controller;

import com.waguwagu.weat.domain.category.model.dto.GetAllCategoryListDTO;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "카테고리", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리 리스트를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "모든 카테고리 리스트 반환",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GetAllCategoryListDTO.Response.class)
            )
    )
    @GetMapping("/")
    public GetAllCategoryListDTO.Response getAllCategoryList() {
        return categoryService.getAllCategoryList();
    }
}
