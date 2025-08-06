package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.LocationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationSettingRepository extends JpaRepository<LocationSetting, Long> {

    @Query("SELECT ls FROM LocationSetting ls " +
            "WHERE ls.analysisSetting.member.memberId = :memberId")
    LocationSetting findByMemberId(@Param("memberId") Long memberId);
}