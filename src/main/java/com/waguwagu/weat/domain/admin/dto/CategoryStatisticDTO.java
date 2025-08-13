package com.waguwagu.weat.domain.admin.dto;

import lombok.Builder;
import lombok.Value;

public class CategoryStatisticDTO {

    private Long categoryTagId;
    private String categoryTagName;
    private Boolean preferred; // true=선호, false=비선호
    private Long cnt;
    private Integer rank;

    @Value
    @Builder
    public class CategorySideChartDTO {
        java.util.List<String> labels;
        java.util.List<Long> counts;
    }

    // 전체 응답
    @Value
    @Builder
    public static class CategoryPreferenceTopDTO {
        CategorySideChartDTO preferred;
        CategorySideChartDTO notPreferred;
    }
}
