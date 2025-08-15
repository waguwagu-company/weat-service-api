package com.waguwagu.weat.domain.analysis.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class AIAnalysisDTO {
    @Data
    @Builder
    public static class Request {
        // 그룹 식별자
        private String groupId;

        // 분석
        private Long analysisId;

        @Builder.Default
        private List<MemberSetting> memberSettingList = new ArrayList<>();

        @Data
        @Builder
        public static class MemberSetting {
            // 사용자 식별자
            private Long memberId;

            // 위치 설정
            @JsonProperty("xPosition")
            private Double xPosition;
            @JsonProperty("yPosition")
            private Double yPosition;

            // 카테고리 호/불호 설정
            @Builder.Default
            private List<MemberSetting.CategorySetting> categoryList = new ArrayList<>();

            // 텍스트 입력 설정
            private String inputText;

            @Data
            @Builder
            public static class CategorySetting {
                private Long categoryId;
                private String categoryName;

                private Long categoryTagId;
                private String categoryTagName;
                @JsonProperty("isPreferred")
                private boolean isPreferred;
            }

        }
    }

    @Data
    @Builder
    public static class Response {

        private String groupId;
        private AnalysisResult analysisResult;

        @Data
        @Builder
        public static class AnalysisResult {

            @Builder.Default
            List<AnalysisResultDetial> analysisResultDetailList = new ArrayList<>();

            @Data
            public static class AnalysisResultDetial {

                private Place place;
                private List<AnalysisBasis> analysisBasisList = new ArrayList<>();
                private String analysisResultDetailContent;

                @Data
                public static class Place {
                    private String placeName;
                    private String placeRoadNameAddress;
                    private List<PlaceImage> placeImageList;
                }

                @Data
                public static class PlaceImage {
                    private String placeImageUrl;
                }

                @Data
                public static class AnalysisBasis {
                    private String analysisBasisType;
                    private String analysisBasisContent;
                }
            }
        }
    }
}
