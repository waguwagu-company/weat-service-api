package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.CategorySetting;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategorySettingRepository extends JpaRepository<CategorySetting, Long> {

    @Query("SELECT cs FROM CategorySetting cs " +
            "WHERE cs.analysisSetting.member.memberId = :memberId")
    List<CategorySetting> findAllByMemberId(@Param("memberId") Long memberId);
}
