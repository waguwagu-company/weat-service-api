package com.waguwagu.weat.domain.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSetting;

import java.util.List;
import java.util.Optional;

public interface AnalysisSettingRepository extends JpaRepository<AnalysisSetting, Long> {
    Boolean existsByMemberMemberId(Long memberId);
}
