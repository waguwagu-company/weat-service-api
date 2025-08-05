package com.waguwagu.weat.domain.analysis.model.entity;

public enum AnalysisStatus {
    NOT_STARTED("시작안함"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    FAILED("실패"),
    CANCELLED("취소");

    private final String description;

    AnalysisStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 