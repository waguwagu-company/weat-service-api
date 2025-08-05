package com.waguwagu.weat.domain.analysis.controller;

import com.waguwagu.weat.domain.analysis.model.dto.SubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.service.AnalysisService;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * 멤버별 분석 설정 제출 API
     */
    @PostMapping("/settings")
    public ResponseDTO<SubmitAnalysisSettingDTO.Response> submitAnalysisSetting(SubmitAnalysisSettingDTO.Request requestDto) {
        return ResponseDTO.of(analysisService.submitAnalysisSetting(requestDto));
    }
}
