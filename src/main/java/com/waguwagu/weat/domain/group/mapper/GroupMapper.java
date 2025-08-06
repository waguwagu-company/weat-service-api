package com.waguwagu.weat.domain.group.mapper;

import com.waguwagu.weat.domain.group.model.dto.GroupResultQueryDTO;
import com.waguwagu.weat.domain.group.model.entity.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapper {

    Group selectGroupById(@Param("groupId") String groupId);

    List<GroupResultQueryDTO> selectGroupAnalysisResult(String groupId);
}
