package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.TextInputSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TextInputSettingRepository extends JpaRepository<TextInputSetting, Long> {

    @Query("SELECT ts FROM TextInputSetting ts " +
            "WHERE ts.analysisSetting.member.memberId = :memberId")
    TextInputSetting findByMemberId(@Param("memberId") Long memberId);

    void deleteByAnalysisSettingDetailId(Long id);

    void deleteAllByAnalysisSettingDetailIdIn(List<Long> detailIds);
}