package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "analysis_basis")
public class AnalysisBasis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_basis_id")
    private Integer analysisBasisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_detail_id", nullable = false)
    private AnalysisResultDetail analysisResultDetail;

    @Column(name = "analysis_basis_type", length = 50)
    private String analysisBasisType;

    @Column(name = "analysis_basis_content", columnDefinition = "text")
    private String analysisBasisContent;

    @Column(name = "analysis_score")
    private Integer analysisScore;
}