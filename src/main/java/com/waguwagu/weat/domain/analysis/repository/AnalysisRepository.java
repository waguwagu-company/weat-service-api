package com.waguwagu.weat.domain.analysis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.waguwagu.weat.domain.analysis.model.entity.Analysis;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findByGroupGroupId(String groupId);
    void deleteAllByGroupGroupId(String groupId);
}
