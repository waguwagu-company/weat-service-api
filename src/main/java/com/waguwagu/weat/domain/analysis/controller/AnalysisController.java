package com.waguwagu.weat.domain.analysis.controller;

import com.waguwagu.weat.domain.analysis.model.dto.*;
import com.waguwagu.weat.domain.analysis.service.AnalysisService;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    @ApiResponse(
            responseCode = "208",
            description = "이미 분석 설정을 제출한 멤버일 경우",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "이미 제출됨",
                            summary = "이미 제출한 회원입니다.",
                            value = """
                {
                  "code": "MEMBER_ALREADY_SUBMIT_SETTING",
                  "message": "이미 제출한 회원입니다.",
                  "data": null
                }
                """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "요청한 리소스를 찾을 수 없을 경우",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "해당하는 멤버 없음",
                                    summary = "존재하지 않는 멤버 식별자",
                                    value = """
                                            {
                                              "code": "MEMBER_NOT_FOUND",
                                              "message": "존재하지 않는 멤버입니다. (memberId: ?)",
                                              "data": null
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/settings")
    public ResponseDTO<SubmitAnalysisSettingDTO.Response> submitAnalysisSetting(
            @RequestBody SubmitAnalysisSettingDTO.Request requestDto) {
        return ResponseDTO.of(analysisService.submitAnalysisSetting(requestDto));
    }


    @Operation(summary = "분석 설정 제출 여부 조회", description = "멤버가 분석 설정을 제출했는지 여부를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IsMemberSubmitAnalysisSettingDTO.Response.class)
            )
    )
    @GetMapping(value = "/settings/status")
    public ResponseDTO<IsMemberSubmitAnalysisSettingDTO.Response> isMemberSubmitAnalysisSetting(@RequestParam("memberId") Long memberId) {
        return ResponseDTO.of(analysisService.isMemberSubmitAnalysisSetting(memberId));
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
    public ResponseDTO<IsAnalysisStartAvailableDTO.Response> isAnalysisStartAvailable(@RequestParam("groupId") String groupId) {
        return ResponseDTO.of(analysisService.isAnalysisStartAvailable(groupId));
    }

    // TODO: 개발 진행중, AI 분석 서비스 응답 형식 정해지면 재개
    @Operation(summary = "분석 시작 요청", description = "분석 시작 조건을 충족한 경우, 요청시점까지 제출된 분석설정을 활용하여 분석을 진행한다.")
    @PostMapping
    public ResponseDTO<AnalysisStartDTO.Response> analysisStart(@RequestBody AnalysisStartDTO.Request request) {
        return ResponseDTO.of(analysisService.analysisStart(request.getGroupId()));
    }


    @Operation(summary = "사용자 입력값 유효성 검증", description = "사용자가 입력한 값의 유효성을 검증한다.")
    @PostMapping("/validation/input")
    public ResponseDTO<ValidationDTO.Response> validateInput(@RequestBody ValidationDTO.Request request) {
        return ResponseDTO.of(analysisService.validateInput(request));
    }
}
