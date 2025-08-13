package com.waguwagu.weat.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SUCCESS(HttpStatus.OK, "SUCCESS", "요청이 성공적으로 처리되었습니다."),
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "존재하지 않는 그룹입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "존재하지 않는 멤버입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다."),
    CATEGORY_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_TAG_NOT_FOUND", "존재하지 않는 카테고리 태그입니다."),
    LOCATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "LOCATION_SETTING_NOT_FOUND","존재하지 않는 위치설정입니다." ),
    ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS_NOT_FOUND", "존재하지 않는 분석입니다."),
    ANALYSIS_RESULT_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS_RESULT_DETAIL_NOT_FOUND", "존재하지 않는 분석결과상세 입니다."),
    MEMBER_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "GROUP_MEMBER_LIMIT", "그룹 정원을 초과했습니다."),
    MEMBER_ALREADY_SUBMIT_SETTING(HttpStatus.ALREADY_REPORTED, "MEMBER_ALREADY_SUBMIT_SETTING", "이미 제출한 회원입니다."),
    ANALYSIS_CONDITION_NOT_SATISFIED(HttpStatus.BAD_REQUEST, "ANALYSIS_CONDITION_NOT_SATISFIED","분석 시작 조건이 충족되지 않았습니다." ),
    ANALYSIS_ALREADY_STARTED(HttpStatus.ALREADY_REPORTED, "ANALYSIS_ALREADY_STARTED", "이미 진행중인 분석입니다."),
    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI_SERVER_ERROR", "AI 서버 호출 중 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
