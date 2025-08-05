package com.waguwagu.weat.domain.analysis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class SubmitAnalysisSettingDTO {

    @Getter
    public static class Request {
        // 제출한 회원 식별자
        Long memberId;
        // 위치 정보
        LocationSettingDTO locationSetting;
        // 카테고리 정보
        List<CategorySettingDTO> categorySettingList;
        // 입력 텍스트 정보
        TextInputSettingDTO textInputSettingDTO;
    }

    @Getter
    @Builder
    public static class Response {
        // 제출한 회원 식별자
        Long memberId;
        // 제출한 분석 설정 식별자
        Long settingId;
    }
}
