package com.waguwagu.weat.domain.admin.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupStatisticQueryDTO {
    private LocalDate date;      // 날짜
    private Long singleCount;    // 단일 멤버 그룹 수
    private Long multiCount;     // 다중 멤버 그룹 수
}