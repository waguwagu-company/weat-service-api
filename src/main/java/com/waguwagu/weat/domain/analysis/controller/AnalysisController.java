package com.waguwagu.weat.domain.analysis.controller;

import com.waguwagu.weat.domain.analysis.model.dto.*;
import com.waguwagu.weat.domain.analysis.service.AnalysisService;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
@Tag(name = "분석", description = "분석 API")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "분석 설정 제출", description = "멤버가 분석을 위한 설정을 제출합니다.\n\n- 위치 설정은 좌표값(`xPosition, yPosition`) 또는 도로명주소(`roadnameAddress`) 중 하나 이상을 반드시 포함해야 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubmitAnalysisSettingDtoResponseWrapper.class)
            )
    )
    @PostMapping("/settings")
    public SubmitAnalysisSettingDtoResponseWrapper submitAnalysisSetting(
            @RequestBody SubmitAnalysisSettingDTO.Request requestDto) {
        return (SubmitAnalysisSettingDtoResponseWrapper) ResponseDTO.of(analysisService.submitAnalysisSetting(requestDto));
    }


    @Operation(summary = "분석 설정 제출 여부 조회", description = "멤버가 분석 설정을 제출했는지 여부를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IsMemberSubmitAnalysisSettingDtoResponseWrapper.class)
            )
    )
    @GetMapping(value = "/settings/status")
    public IsMemberSubmitAnalysisSettingDtoResponseWrapper isMemberSubmitAnalysisSetting(@RequestParam("memberId") Long memberId) {
        return (IsMemberSubmitAnalysisSettingDtoResponseWrapper) ResponseDTO.of(analysisService.isMemberSubmitAnalysisSetting(memberId));
    }


    @Operation(summary = "분석 시작가능조건 충족 여부 조회", description = "분석을 시작할 수 있는 조건을 만족했는지 여부를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IsAnalysisStartAvailableDtoResponseWrapper.class)
            )
    )
    @GetMapping("/status")
    public IsAnalysisStartAvailableDtoResponseWrapper isAnalysisStartAvailable(@RequestParam("groupId") String groupId) {
        return (IsAnalysisStartAvailableDtoResponseWrapper) ResponseDTO.of(analysisService.isAnalysisStartAvailable(groupId));
    }
}
