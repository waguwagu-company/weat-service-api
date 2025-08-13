package com.waguwagu.weat.domain.admin.mapper;


import com.waguwagu.weat.domain.admin.dto.GroupStatisticQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    // 대시보드 분석추이 통계
    List<GroupStatisticQueryDTO> selectRecent7DaysCounts(@Param("tz") String tz);
}
