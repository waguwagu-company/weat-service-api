package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisBasis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisBasisRepository extends JpaRepository<AnalysisBasis, Long> {
    void deleteAllByAnalysisResultDetail(AnalysisResultDetail analysisResultDetail);
}
