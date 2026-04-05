package com.OBE.workflow.feature.department;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.department.request.DepartmentFilterRequest;
import com.OBE.workflow.feature.department.request.DepartmentRequest;
import com.OBE.workflow.feature.department.response.DepartmentResponse;
import com.OBE.workflow.feature.department.response.DepartmentSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<Department, DepartmentResponse>>> getDepartments(@ParameterObject Pageable pageable, @RequestBody DepartmentFilterRequest departmentFilterRequest) {
        Page<Department> page = departmentService.getDepartments(pageable, departmentFilterRequest);
        // Cách viết hiện đại cho Java 21
        List<DepartmentResponse> responseList = page.getContent().stream()
                .map(departmentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<Department, DepartmentResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách khoa/phòng ban")
                        .data(PageResponse.fromPage(page, responseList))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("@ps.hasPermission('DEPT_CREATE', null, null)")
    public ResponseEntity<ApiResponse<?>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        Department savedDepartment = departmentService.createDepartment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Đã tạo trường hoặc khoa")
                .data(departmentMapper.toResponse(savedDepartment))
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('DEPT_WRITE', null, #a0)")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable("id") String id,
            @Valid @RequestBody DepartmentRequest request) {
        // Gọi service xử lý (Sử dụng MapStruct như ta đã bàn ở bước trước)
        Department updatedDepartment = departmentService.updateDepartment(id, request);

        return ResponseEntity.ok(
                ApiResponse.<DepartmentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin khoa thành công")
                        .data(departmentMapper.toResponse(updatedDepartment))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('DEPT_DELETE', null, null)")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable("id") String id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa khoa/phòng ban thành công")
                        .build()
        );
    }

    @GetMapping("/all/summary")
    public ResponseEntity<ApiResponse<List<DepartmentSummaryResponse>>> getAllSummaries() {
        // 1. Lấy toàn bộ Entity từ Database
        List<Department> departments = departmentRepository.findAll();

        // 2. Chuyển đổi sang DTO rút gọn bằng phương thức static fromEntity đã viết
        List<DepartmentSummaryResponse> summaries = departments.stream()
                .map(DepartmentSummaryResponse::fromEntity)
                .toList();

        // 3. Trả về Response chuẩn
        return ResponseEntity.ok(
                ApiResponse.<List<DepartmentSummaryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách rút gọn các khoa thành công")
                        .data(summaries)
                        .build()
        );
    }
}
