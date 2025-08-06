package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "Group Result DTO", description = "그룹별 분석 결과 조회 DTO")
public class GroupResultDTO {


    @Builder
    @Getter
    public static class Response {
        @Schema(description = "그룹별 분석 결과 목록")
        List<GroupResultDetailDTO> groupResultDetailList;
    }
}
