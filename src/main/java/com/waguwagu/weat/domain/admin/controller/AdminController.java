package com.waguwagu.weat.domain.admin.controller;

import com.waguwagu.weat.domain.admin.dto.GetGroupListDTO;
import com.waguwagu.weat.domain.admin.service.AdminService;
import com.waguwagu.weat.domain.group.model.entity.Group;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * 특정 그룹 ID에 해당하는 모든 분석 및 관련 데이터를 삭제합니다.
     *
     * @param groupId 삭제할 그룹 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupId) {
        adminService.deleteGroupById(groupId);
        return ResponseEntity.ok("그룹 삭제 완료: " + groupId);
    }


    @GetMapping("/group")
    public ResponseEntity<GetGroupListDTO.Response> getAllGroupList(){
        return ResponseEntity.ok(adminService.getGroupList());
    }
}