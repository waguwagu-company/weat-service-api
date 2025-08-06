package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "analysis_result_detail")
public class AnalysisResultDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_result_detail_id")
    private Integer analysisResultDetailId;

    @Column(name = "analysis_result_id", nullable = false)
    private Integer analysisResultId; // 추후 연관관계 설정 가능 (예: @ManyToOne)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "analysis_result_detail_content", columnDefinition = "text")
    private String analysisResultDetailContent;
}