package com.OBE.workflow.feature.supDepartment;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.supDepartment.request.SubDepartmentRequest;
import com.OBE.workflow.feature.supDepartment.response.SubDepartmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sub-departments")
@RequiredArgsConstructor
public class SubDepartmentController {
    private final SubDepartmentService subDepartmentService;
    private final SubDepartmentMapper subDepartmentMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<SubDepartmentResponse>> createSubDepartment(@Valid @RequestBody SubDepartmentRequest request) {
        SubDepartment savedSubDepartment = subDepartmentService.createSubDepartment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SubDepartmentResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Đã tạo bộ môn mới thành công")
                .data(subDepartmentMapper.toResponse(savedSubDepartment))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubDepartmentResponse>> updateSubDepartment(
            @PathVariable String id,
            @Valid @RequestBody SubDepartmentRequest request) {

        SubDepartment updatedSubDepartment = subDepartmentService.updateSubDepartment(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SubDepartmentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin bộ môn hoặc khoa thành công")
                        .data(subDepartmentMapper.toResponse(updatedSubDepartment))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubDepartment(@PathVariable String id) {
        subDepartmentService.deleteSubDepartment(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa bộ môn hoặc khoa thành công")
                        .build()
        );
    }
}