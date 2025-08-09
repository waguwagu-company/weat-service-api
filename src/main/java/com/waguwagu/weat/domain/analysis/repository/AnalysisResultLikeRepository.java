package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResultDetail;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResultLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnalysisResultLikeRepository extends JpaRepository<AnalysisResultLike, Long> {

    @Query("""
                SELECT arl
                FROM AnalysisResultLike arl
                WHERE arl.member.memberId = :memberId
                  AND arl.analysisResultDetail.analysisResultDetailId = :analysisResultDetailId
            """)
    Optional<AnalysisResultLike> findByAnalysisResultDetailIdAndMemberId(
            @Param("analysisResultDetailId") Long analysisResultDetailId,
            @Param("memberId") Long memberId
    );


    Long countByAnalysisResultDetail(AnalysisResultDetail detail);

}
