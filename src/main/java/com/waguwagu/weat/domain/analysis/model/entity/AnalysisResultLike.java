package com.waguwagu.weat.domain.analysis.model.entity;

import com.waguwagu.weat.domain.group.model.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "analysis_result_like")
public class AnalysisResultLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_result_like_id")
    private Long analysisResultLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_detail_id", nullable = false)
    private AnalysisResultDetail analysisResultDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}