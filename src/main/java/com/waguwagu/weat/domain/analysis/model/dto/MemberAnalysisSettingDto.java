package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberAnalysisSettingDTO {
    private Long memberId;
    private Double xPosition;
    private Double yPosition;
    private String roadnameAddress;
    private String inputText;
    private List<CategorySetting> categorySettings;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CategorySetting {
        private Long categoryId;
        private String categoryName;
        private Long categoryTagId;
        private String categoryTagName;
        private Boolean isPreferred;
    }
}

