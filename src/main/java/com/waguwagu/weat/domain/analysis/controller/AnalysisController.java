package com.waguwagu.weat.domain.analysis.controller;

import com.waguwagu.weat.domain.analysis.model.dto.SubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.service.AnalysisService;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
@Tag(name = "분석", description = "분석 API")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(
        summary = "분석 설정 제출",
        description = "멤버가 분석을 위한 설정을 제출합니다.\n\n- 위치 설정은 좌표값(`xPosition, yPosition`) 또는 도로명주소(`roadnameAddress`) 중 하나 이상을 반드시 포함해야 합니다."
    )
    @PostMapping("/settings")
    public ResponseDTO<SubmitAnalysisSettingDTO.Response> submitAnalysisSetting(
        @RequestBody SubmitAnalysisSettingDTO.Request requestDto
    ) {
        return ResponseDTO.of(analysisService.submitAnalysisSetting(requestDto));
    }
}
