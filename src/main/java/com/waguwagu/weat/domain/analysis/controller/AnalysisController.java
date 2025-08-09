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
    @GetMapping(value = "/settings/status")
    public ResponseDTO<IsMemberSubmitAnalysisSettingDTO.Response> isMemberSubmitAnalysisSetting(@RequestParam("memberId") Long memberId) {
        return ResponseDTO.of(analysisService.isMemberSubmitAnalysisSetting(memberId));
    }

    @Operation(summary = "분석 상태 조회", description =
            "그룹별로 분석 시작 가능 여부, 분석 진행 상태 등 분석의 전반적인 상태를 조회한다.\n\n" +
                    "진행 상태(`analysisStatus`)\n" +
                    "   - NOT_STARTED : 분석이 시작되지 않음" +
                    "   - IN_PROGRESS : 분석 진행중" +
                    "   - COMPLETED : 분석 완료" +
                    "   - FAILED : 분석 실패")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IsAnalysisStartAvailableDtoResponseWrapper.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "요청한 리소스를 찾을 수 없을 경우",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "해당하는 그룹 없음",
                            summary = "존재하지 않는 그룹 식별자",
                            value = """
                                    {
                                      "code": "GROUP_NOT_FOUND",
                                      "message": "존재하지 않는 그룹입니다. (groupId: ?)",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/status")
    public ResponseDTO<GetAnalysisStatusDTO.Response> getAnalysisStatus(@RequestParam("groupId") String groupId) {
        return ResponseDTO.of(analysisService.getAnalysisStatus(groupId));
    }

    // TODO: 개발 진행중, AI 분석 서비스 응답 형식 정해지면 재개
    @Operation(summary = "분석 시작 요청", description = "분석 시작 조건을 충족한 경우, 요청시점까지 제출된 분석설정을 활용하여 분석을 진행한다.\n" +
            "- 단체로 참여하는 경우 `2명 이상이 분석 설정을 제출`해야만 분석을 시작할 수 있다.")
    @ApiResponse(
            responseCode = "200",
            description = "분석 시작 요청 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AnalysisStartDTOResponseWrapper.class)
            )
    )
    @ApiResponse(
            responseCode = "208",
            description = "이미 분석이 시작된 그룹일 경우",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "이미 시작됨",
                            summary = "분석이 이미 시작된 그룹",
                            value = """
                                    {
                                      "code": "ANALYSIS_ALREADY_STARTED",
                                      "message": "이미 진행중인 분석입니다.",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "분석 시작 조건 불충족",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "분석 조건 불충족",
                            summary = "제출된 설정이 부족함",
                            value = """
                                    {
                                      "code": "ANALYSIS_CONDITION_NOT_SATISFIED",
                                      "message": "분석 시작 조건이 충족되지 않았습니다.",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "요청한 리소스를 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "GROUP_NOT_FOUND",
                                    summary = "존재하지 않는 그룹 ID",
                                    value = """
                                            {
                                              "code": "GROUP_NOT_FOUND",
                                              "message": "존재하지 않는 그룹입니다. (groupId: ?)",
                                              "data": null
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping
    public ResponseDTO<AnalysisStartDTO.Response> analysisStart(@RequestBody AnalysisStartDTO.Request request) {
        return ResponseDTO.of(analysisService.analysisStart(request));
    }


    @Operation(summary = "사용자 입력값 유효성 검증", description = "사용자가 입력한 값의 유효성을 검증한다.")
    @PostMapping("/validation/input")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationDTOResponseWrapper.class)
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "AI 서버 응답 오류",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "AI_SERVER_ERROR",
                                    summary = "AI 서버 호출 중 오류 발셍",
                                    value = """
                                            {
                                              "code": "AI_SERVER_ERROR",
                                              "message": "AI 서버 호출 중 오류가 발생했습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseDTO<ValidationDTO.Response> validateInput(@RequestBody ValidationDTO.Request request) {
        return ResponseDTO.of(analysisService.validateInput(request));
    }

    @Operation(summary = "분석결과상세(장소)별 좋아요 토글", description = "분석결과의 각 장소에 대해 좋아요를 활성화 또는 비활성화한다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ToggleAnalysisResultDetailLikeResponseWrapper.class)
            )
    )
    @PostMapping("/likes")
    public ResponseDTO<ToggleAnalysisResultDetailLikeDTO.Response> toggleAnalysisDetail(@RequestBody ToggleAnalysisResultDetailLikeDTO.Request request) {
        return ResponseDTO.of(analysisService.toggleAnalysisResultDetailLike(request));
    }

    @Operation(summary = "분석결과상세(장소)별 좋아요 개수 조회", description = "분석결과의 각 장소에 대한 좋아요 개수를 조회한다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ToggleAnalysisResultDetailLikeResponseWrapper.class)
            )
    )
    @GetMapping("/likes")
    public ResponseDTO<GetAnalysisResultLikeCountDTO.Response> getAnalysisDetailLikeCount(@RequestParam("analysisDetailId") Long analysisDetailId) {
        return ResponseDTO.of(analysisService.getAnalysisResultLikeCount(analysisDetailId));
    }
}
