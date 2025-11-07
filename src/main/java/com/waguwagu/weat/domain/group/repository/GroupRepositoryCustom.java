package com.waguwagu.weat.domain.group.repository;

import com.waguwagu.weat.domain.group.model.dto.GroupAnalysisBasisQueryDTO;

import java.util.List;

public interface GroupRepositoryCustom {
    List<GroupAnalysisBasisQueryDTO> findGroupAnalysisBasis(String groupId);

}
