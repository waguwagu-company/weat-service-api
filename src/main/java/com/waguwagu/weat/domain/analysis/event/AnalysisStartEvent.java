package com.waguwagu.weat.domain.analysis.event;

import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO.Request.MemberSetting;

import java.util.List;

public record AnalysisStartEvent(
        String groupId,
        Long analysisId,
        List<MemberSetting> memberSettingList) {
}
