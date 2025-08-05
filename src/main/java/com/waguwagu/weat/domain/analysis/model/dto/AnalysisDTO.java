package com.waguwagu.weat.domain.analysis.model.dto;

import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus;
import com.waguwagu.weat.domain.group.model.entity.Group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisDTO {
    private Long analysisId;
    private Group group;
    private AnalysisStatus analysisStatus;

    public static AnalysisDTO of(Analysis analysis) {
        return AnalysisDTO.builder()
                .analysisId(analysis.getAnalysisId())
                .group(analysis.getGroup())
                .analysisStatus(analysis.getAnalysisStatus())
                .build();
    }
}
