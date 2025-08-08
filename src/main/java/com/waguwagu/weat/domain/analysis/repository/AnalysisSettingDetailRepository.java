package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSetting;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisSettingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisSettingDetailRepository extends JpaRepository<AnalysisSettingDetail, Long> {
    List<AnalysisSettingDetail> findAllByAnalysisSetting(AnalysisSetting analysisSetting);
}
