package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.dto.MemberAnalysisSettingDTO;

import java.util.List;

public interface AnalysisSettingRepositoryCustom {
    List<MemberAnalysisSettingDTO> findMemberAnalysisSettingsByGroupId(String groupId);
    Long countAnalysisSettingByGroupId(String groupId);
}
