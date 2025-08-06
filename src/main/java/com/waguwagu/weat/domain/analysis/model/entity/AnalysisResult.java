package com.waguwagu.weat.domain.analysis.model.entity;

import com.waguwagu.weat.domain.group.model.entity.Group;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_result_id")
    private Integer analysisResultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;
}