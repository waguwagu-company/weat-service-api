package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByGroupGroupId(String groupId);
}
