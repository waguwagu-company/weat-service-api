package com.waguwagu.weat.domain.group.controller;

import com.waguwagu.weat.domain.group.model.dto.CreateGroupDTO;
import com.waguwagu.weat.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group")
@Tag(name = "Group Controller", description = "그룹 및 멤버 관련 API 요청 처리")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping(value = "/create", produces = "application/json")
    public ResponseEntity<CreateGroupDTO.Response> createGroup() {
        return ResponseEntity.ok(groupService.createGroup());
    }
}
