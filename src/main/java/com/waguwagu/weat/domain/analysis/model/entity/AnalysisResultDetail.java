package com.waguwagu.weat.domain.analysis.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "analysis_result_detail_content", columnDefinition = "text")
    private String analysisResultDetailContent;

    @Column(name = "analysis_result_detail_template_message", columnDefinition = "text")
    private String analysisResultDetailTemplateMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_result_keywords", columnDefinition = "jsonb", nullable = false)
    private List<String> analysisResultKeywords;

}