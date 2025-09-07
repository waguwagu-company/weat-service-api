package com.waguwagu.weat.domain.group.mapper;

import com.waguwagu.weat.domain.group.model.dto.GroupAnalysisBasisQueryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {
    List<GroupAnalysisBasisQueryDTO> selectGroupAnalysisBasis(String groupId);

}
