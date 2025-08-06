package com.waguwagu.weat.domain.group.mapper;

import com.waguwagu.weat.domain.group.model.entity.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupMapper {

    Group selectGroupById(@Param("groupId") String groupId);
}
