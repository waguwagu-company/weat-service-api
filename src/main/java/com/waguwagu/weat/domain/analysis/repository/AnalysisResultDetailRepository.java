package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResult;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisResultDetailRepository extends JpaRepository<AnalysisResultDetail, Long> {
    List<AnalysisResultDetail> findAllByAnalysisResult(AnalysisResult analysisResult);
}
