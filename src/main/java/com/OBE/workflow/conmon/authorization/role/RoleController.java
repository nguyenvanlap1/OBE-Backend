package com.OBE.workflow.conmon.authorization.role;

import com.OBE.workflow.conmon.authorization.role.request.RoleFilterRequest;
import com.OBE.workflow.conmon.authorization.role.request.RoleRequestDetail;
import com.OBE.workflow.conmon.authorization.role.response.RoleResponse;
import com.OBE.workflow.conmon.authorization.role.response.RoleResponseDetail;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDetail>> createRole(@Valid @RequestBody RoleRequestDetail request) {
        RoleResponseDetail savedRole = roleService.createRole(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<RoleResponseDetail>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo vai trò mới thành công")
                        .data(savedRole)
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponse, RoleResponse>>> getRoles(
            @ParameterObject Pageable pageable,
            @RequestBody RoleFilterRequest filterRequest) {

        Page<RoleResponse> page = roleService.getRoles(pageable, filterRequest);

        // Vì Service đã map sang DTO (RoleResponseDetail) nên ta lấy trực tiếp list
        List<RoleResponse> responseList = page.getContent();

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<RoleResponse, RoleResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách vai trò hệ thống")
                        .data(PageResponse.fromPage(page, responseList))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDetail>> getRoleDetail(@PathVariable("id") String id) {
        RoleResponseDetail detail = roleService.getRoleDetail(id);
        return ResponseEntity.ok(
                ApiResponse.<RoleResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Chi tiết vai trò hệ thống")
                        .data(detail)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDetail>> updateRole(
            @PathVariable("id") String id,
            @Valid @RequestBody RoleRequestDetail request) {

        RoleResponseDetail updatedRole = roleService.updateRole(id, request);

        return ResponseEntity.ok(
                ApiResponse.<RoleResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật vai trò thành công")
                        .data(updatedRole)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable("id") String id) {
        roleService.deleteRole(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa vai trò thành công")
                        .build()
        );
    }
}