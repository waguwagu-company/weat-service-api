package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class AIAnalysisDTO {
    @Getter
    @Builder
    public static class Request {

        private String groupId;

        @Builder.Default
        private List<MemberSetting> memberSettingList = new ArrayList<>();

        @Builder
        @Getter
        public static class MemberSetting {
            // 사용자 식별자
            private Long memberId;

            // 위치 설정
            private Double xPosition;
            private Double yPosition;

            // 카테고리 호/불호 설정
            @Builder.Default
            private List<MemberSetting.Category> categoryList = new ArrayList<>();

            // 텍스트 입력 설정
            private String inputText;

            @Getter
            @Builder
            public static class Category {
                private int categoryId;
                private String categoryName;
            }

        }
    }

    @Getter
    public static class Response {

        private Long groupId;
        private Long analysisId;
        private AnalysisResult analysisResult;

        @Getter
        public static class AnalysisResult {

            List<AnalysisResultDetial> analysisResultDetailList = new ArrayList<>();

            @Getter
            public static class AnalysisResultDetial {

                private Place place;
                private List<AnalysisBasis> analysisBasisList;
                private String analysisResultDetailContent;

                @Getter
                public static class Place {
                    private Long placeId;
                    private String placeName;
                    private String placeRoadNameAddress;

                    private List<String> placeImageUrlList = new ArrayList<>();
                    private List<Byte[]> placeImageDataList = new ArrayList<>();
                }

                @Getter
                public static class AnalysisBasis {
                    private String analysisBasisType;
                    private String analysisBasisContent;
                }
            }
        }
    }
}
