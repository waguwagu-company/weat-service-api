package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAnalysisSettingDto {
    private Long memberId;
    private Double xPosition;
    private Double yPosition;
    private String roadnameAddress;
    private String inputText;
    private List<CategorySettingDto> categorySettings;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySettingDto {
        private Long categoryId;
        private String categoryName;
        private Long categoryTagId;
        private String categoryTagName;
        private Boolean isPreferred;
    }
}

