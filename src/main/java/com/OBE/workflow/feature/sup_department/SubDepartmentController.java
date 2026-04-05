package com.OBE.workflow.feature.sup_department;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.feature.sup_department.request.SubDepartmentFilterRequest;
import com.OBE.workflow.feature.sup_department.request.SubDepartmentRequest;
import com.OBE.workflow.feature.sup_department.response.SubDepartmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sub-departments")
@RequiredArgsConstructor
public class SubDepartmentController {
    private final SubDepartmentService subDepartmentService;
    private final SubDepartmentMapper subDepartmentMapper;

    @PostMapping
    @PreAuthorize("@ps.hasPermission('SUBDEPT_CREATE', null, #request.departmentId)")
    public ResponseEntity<ApiResponse<SubDepartmentResponse>> createSubDepartment(@P("request") @Valid @RequestBody SubDepartmentRequest request) {
        SubDepartment savedSubDepartment = subDepartmentService.createSubDepartment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SubDepartmentResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Đã tạo bộ môn mới thành công")
                .data(subDepartmentMapper.toResponse(savedSubDepartment))
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('SUBDEPT_WRITE', #id, #request.departmentId)")
    public ResponseEntity<ApiResponse<SubDepartmentResponse>> updateSubDepartment(
            @P("id") @PathVariable("id") String id,
            @P("request") @Valid @RequestBody SubDepartmentRequest request) {

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
    @PreAuthorize("@ps.hasPermission('SUBDEPT_DELETE', #id, @subDepartmentService.getDepartmentIdBySubDepartmentId(#id))")
    public ResponseEntity<ApiResponse<Void>> deleteSubDepartment(@P("id") @PathVariable("id") String id) {
        subDepartmentService.deleteSubDepartment(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa bộ môn hoặc khoa thành công")
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SubDepartment, SubDepartmentResponse>>> searchSubDepartments(
            @org.springdoc.core.annotations.ParameterObject org.springframework.data.domain.Pageable pageable,
            @RequestBody SubDepartmentFilterRequest filter) {

        // 1. Lấy dữ liệu phân trang từ Service (Trả về Page<SubDepartment>)
        Page<SubDepartment> page = subDepartmentService.getSubDepartments(pageable, filter);

        // 2. Map danh sách Entity sang Response DTO (Sử dụng Stream Java 16+ / Java 21)
        List<SubDepartmentResponse> responseList = page.getContent().stream()
                .map(subDepartmentMapper::toResponse)
                .toList();

        // 3. Trả về ApiResponse chứa PageResponse đã được format chuẩn cho Frontend
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<SubDepartment, SubDepartmentResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách bộ môn/phòng ban")
                        .data(PageResponse.fromPage(page, responseList))
                        .build()
        );
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<SubDepartmentResponse>>> getByDepartment(@PathVariable("departmentId") String departmentId) {
        List<SubDepartment> subDepartments = subDepartmentService.getByDepartmentId(departmentId);

        // Map sang DTO để trả về
        List<SubDepartmentResponse> responses = subDepartments.stream()
                .map(subDepartmentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.<List<SubDepartmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build());
    }
}