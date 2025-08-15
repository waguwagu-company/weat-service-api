package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.LocationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocationSettingRepository extends JpaRepository<LocationSetting, Long> {

    @Query(value = """
        SELECT
             ls.analysis_setting_detail_id,
             ls.x_position,
             ls.y_position,
             ls.roadname_address,
             asd.analysis_setting_id  
        FROM 
             analysis_setting aset
        JOIN 
             analysis_setting_detail asd ON 
             asd.analysis_setting_id = aset.analysis_setting_id
        JOIN 
             location_setting ls ON 
             ls.analysis_setting_detail_id = asd.analysis_setting_detail_id
        WHERE 
             aset.member_id = :memberId
        ORDER BY 
             ls.analysis_setting_detail_id DESC
        LIMIT 1
        """,
    nativeQuery = true)
    Optional<LocationSetting> findByMemberId(@Param("memberId") Long memberId);

    void deleteAllByAnalysisSettingDetailIdIn(List<Long> detailIds);
}