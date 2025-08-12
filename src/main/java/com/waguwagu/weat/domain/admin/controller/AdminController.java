package com.waguwagu.weat.domain.admin.controller;

import com.waguwagu.weat.domain.admin.dto.CreateCategoryTagDTO;
import com.waguwagu.weat.domain.admin.dto.DeleteCategoryTagDTO;
import com.waguwagu.weat.domain.admin.dto.GetGroupListDTO;
import com.waguwagu.weat.domain.admin.dto.RenameCategoryTagDTO;
import com.waguwagu.weat.domain.admin.service.AdminService;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping(value = "/group", params = {"!page", "!size", "!sort", "!order"})
    public ResponseEntity<GetGroupListDTO.Response> getAllGroupList() {
        return ResponseEntity.ok(adminService.getGroupList());
    }

    @GetMapping(value = "/group", params = {"page"})
    public ResponseEntity<GetGroupListDTO.Response> getAllGroupListWithPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order
    ) {
        Sort.Direction dir = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        return ResponseEntity.ok(adminService.getGroupListWithPaging(pageable, sort, order));
    }

//    @GetMapping("/group/count")
//    public ResponseEntity<Long> getAllGroupCount() {
//        return ResponseEntity.ok(adminService.getGroupCount());
//    }

    @PutMapping("/categoryTags")
    public ResponseDTO<RenameCategoryTagDTO.Response> renameCategoryTag(
            @RequestBody RenameCategoryTagDTO.Request request) {
        return ResponseDTO.of(adminService.renameCategoryTag(request));
    }

    @DeleteMapping("/categoryTags/{categoryTagId}")
    public ResponseDTO<DeleteCategoryTagDTO.Response> deleteCategoryTag
            (@PathVariable("categoryTagId") Long categoryTagId) {
        return ResponseDTO.of(adminService.deleteCategoryTag(categoryTagId));
    }

    @PostMapping("/categories/{categoryId}/categoryTags")
    public ResponseDTO<CreateCategoryTagDTO.Response> createCategoryTag(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody CreateCategoryTagDTO.Request request) {
        return ResponseDTO.of(adminService.createCategoryTag(categoryId, request));
    }
}
