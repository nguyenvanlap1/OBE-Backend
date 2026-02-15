package com.OBE.workflow.controller;

import com.OBE.workflow.dto.request.DepartmentFilterRequest;
import com.OBE.workflow.dto.response.DepartmentResponse;
import com.OBE.workflow.dto.response.PageResponse;
import com.OBE.workflow.mapper.DepartmentMapper;
import com.OBE.workflow.repository.DepartmentRepository;
import com.OBE.workflow.dto.request.DepartmentRequest;
import com.OBE.workflow.dto.response.ApiResponse;
import com.OBE.workflow.entity.Department;
import com.OBE.workflow.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        Department savedDepartment = departmentService.createDepartment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Đã tạo trường hoặc khoa")
                .data(departmentMapper.toResponse(savedDepartment))
                .build());
    }
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa khoa/phòng ban thành công")
                        .build()
        );
    }
}
