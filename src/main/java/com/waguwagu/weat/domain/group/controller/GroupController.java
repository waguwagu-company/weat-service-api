package com.waguwagu.weat.domain.group.controller;

import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import com.waguwagu.weat.domain.group.model.dto.CreateGroupDTO;
import com.waguwagu.weat.domain.group.model.dto.JoinGroupDTO;
import com.waguwagu.weat.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
@Tag(name = "Group Controller", description = "그룹 및 멤버 관련 API 요청 처리")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    @PostMapping("/")
    public ResponseEntity<ResponseDTO<CreateGroupDTO.Response>> createGroup() {
        return ResponseEntity.ok(ResponseDTO.of(groupService.createGroup()));
    }

    @Operation(summary = "그룹 참여", description = "생성된 그룹에 참여합니다.")
    @PostMapping("/{groupId}/members")
    public ResponseEntity<ResponseDTO<JoinGroupDTO.Response>> joinGroup(@PathVariable String groupId) {
        return ResponseEntity.ok(ResponseDTO.of(groupService.joinGroup(groupId)));
    }
}
