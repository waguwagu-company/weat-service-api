package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisBasis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisBasisRepository extends JpaRepository<AnalysisBasis, Long> {
    void deleteAllByAnalysisResultDetail(AnalysisResultDetail analysisResultDetail);

    void deleteAllByAnalysisResultDetailIn(List<AnalysisResultDetail> resultDetails);
}
