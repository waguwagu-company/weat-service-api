package com.waguwagu.weat.domain.analysis.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Schema(description = "분석설정 제출 요청/응답")
public class SubmitAnalysisSettingDTO {

    @Getter
    @Schema(description = "분석설정 제출 요청")
    public static class Request {
        @NotNull
        @Schema(description = "제출한 회원 식별자", example = "1")
        private Long memberId;

        @Schema(description = "위치 설정")
        private LocationSetting locationSetting;

        @Schema(description = "카테고리 설정 리스트")
        private List<CategorySetting> categorySettingList;

        @Schema(description = "입력 텍스트 설정")
        private TextInputSetting textInputSetting;

        /**
         * xPosition, yPosition 필드가 Swagger 에서 표출 시 xposition, yposition 로 표현되는 직렬화 이슈로 인해 @Data 및 @JsonProperty 임시 적용
         */
        @Data
        @Schema(description = "위치 설정")
        public static class LocationSetting {
            @JsonProperty("xPosition")
            @Schema(description = "위도", example = "37.5666102", name = "xPosition")
            private Double xPosition;

            @JsonProperty("yPosition")
            @Schema(description = "경도", example = "126.9783881")
            private Double yPosition;

            @Schema(description = "도로명 주소", example = "서울특별시 중구 세종대로 110")
            private String roadnameAddress;
        }

        @Getter
        @Schema(description = "카테고리 설정")
        public static class CategorySetting {
            @Schema(description = "카테고리 식별자", example = "1")
            private Long categoryId;

            @Schema(description = "호/불호 여부", example = "true")
            private Boolean isPreferred;
        }

        @Getter
        @Schema(description = "입력 텍스트 설정")
        public static class TextInputSetting {
            @Schema(description = "입력 텍스트", example = "오늘 날씨 어때?")
            private String inputText;
        }
    }

    @Getter
    @Builder
    @Schema(description = "분석 설정 제출 응답")
    public static class Response {
        @Schema(description = "제출한 회원 식별자", example = "1")
        private Long memberId;

        @Schema(description = "제출된 분석 설정 식별자", example = "123")
        private Long analysisSettingId;
    }
}
