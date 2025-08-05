package com.waguwagu.weat.domain.analysis.controller;

import com.waguwagu.weat.domain.analysis.model.dto.AnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.model.dto.IsMemberSubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.model.dto.SubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.service.AnalysisService;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
@Tag(name = "분석", description = "분석 API")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "분석 설정 제출", description = "멤버가 분석을 위한 설정을 제출합니다.\n\n- 위치 설정은 좌표값(`xPosition, yPosition`) 또는 도로명주소(`roadnameAddress`) 중 하나 이상을 반드시 포함해야 합니다.")
    @PostMapping("/settings")
    public ResponseDTO<SubmitAnalysisSettingDTO.Response> submitAnalysisSetting(
            @RequestBody SubmitAnalysisSettingDTO.Request requestDto) {
        return ResponseDTO.of(analysisService.submitAnalysisSetting(requestDto));
    }

    @Operation(summary = "분석 설정 제출 여부 조회", description = "멤버가 분석 설정을 제출했는지 여부를 조회합니다.")
    @GetMapping(value = "/settings/status")
    public ResponseDTO<IsMemberSubmitAnalysisSettingDTO.Response> isMemberSubmitAnalysisSetting(@RequestParam("memberId") Long memberId){
        return ResponseDTO.of(analysisService.isMemberSubmitAnalysisSetting(memberId));
    }
}
