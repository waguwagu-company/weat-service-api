package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.dto.MemberAnalysisSettingDto;

import java.util.List;

public interface AnalysisSettingRepositoryCustom {
    List<MemberAnalysisSettingDto> findMemberAnalysisSettingsByGroupId(String groupId);
}
