package com.waguwagu.weat.domain.group.controller;

import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import com.waguwagu.weat.domain.group.model.dto.*;
import com.waguwagu.weat.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
@Tag(name = "Group Controller", description = "그룹 및 멤버 관련 API 요청 처리")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateGroupDtoResponseWrapper.class)
            )
    )
    @PostMapping("/")
    public ResponseDTO<CreateGroupDTO.Response> createGroup(@RequestBody CreateGroupDTO.Request request) {
        return ResponseDTO.of(groupService.createGroup(request));
    }

    @Operation(summary = "그룹 참여", description = "생성된 그룹에 참여합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JoinGroupDtoResponseWrapper.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "요청한 리소스를 찾을 수 없을 경우",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
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
                    }
            )
    )
    @PostMapping("/{groupId}/members")
    public ResponseDTO<JoinGroupDTO.Response> joinGroup(@PathVariable("groupId") String groupId) {
        return ResponseDTO.of(groupService.joinGroup(groupId));
    }


    @Operation(summary = "분석 결과 조회", description = "그룹별 분석 결과 조회")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GroupResultDtoResponseWrapper.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "요청한 리소스를 찾을 수 없을 경우",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
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
                    }
            )
    )
    @PostMapping("/{groupId}/result")
    public ResponseDTO<GroupResultDTO.Response> getGroupResult(@PathVariable("groupId") String groupId) {
        return ResponseDTO.of(groupService.getGroupResult(groupId));
    }
}
