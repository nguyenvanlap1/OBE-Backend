package com.OBE.workflow.feature.student_class;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.feature.student_class.request.StudentClassCreateRequest;
import com.OBE.workflow.feature.student_class.request.StudentClassFilterRequest;
import com.OBE.workflow.feature.student_class.request.StudentClassUpdateRequest;
import com.OBE.workflow.feature.student_class.response.StudentClassResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassesService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentClassResponse>> createClass(
            @Valid @RequestBody StudentClassCreateRequest request) {

        StudentClassResponse savedClass = studentClassesService.createClass(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StudentClassResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo lớp sinh viên mới thành công")
                        .data(savedClass)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentClassResponse>> updateClass(
            @PathVariable("id") String id,
            @Valid @RequestBody StudentClassUpdateRequest request) {

        StudentClassResponse updatedClass = studentClassesService.updateClass(id, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentClassResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin lớp sinh viên [" + id + "] thành công")
                        .data(updatedClass)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable("id") String id) {
        studentClassesService.deleteClass(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa lớp sinh viên [" + id + "] thành công")
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<StudentClassResponse, StudentClassResponse>>> searchStudentClasses(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable,
            @RequestBody StudentClassFilterRequest filter) {

        // Lấy dữ liệu phân trang từ Service (Service đã map sang Response DTO)
        Page<StudentClassResponse> pageResponse = studentClassesService.getStudentClasses(pageable, filter);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<StudentClassResponse, StudentClassResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách lớp sinh viên")
                        .data(PageResponse.fromPage(pageResponse, pageResponse.getContent()))
                        .build()
        );
    }
}