package com.OBE.workflow.feature.student;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.student.request.StudentCreateRequest;
import com.OBE.workflow.feature.student.request.StudentFilterRequest;
import com.OBE.workflow.feature.student.request.StudentUpdateRequest;
import com.OBE.workflow.feature.student.response.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // --- Lấy danh sách có Phân trang và Lọc ---
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<StudentResponse>>> getStudents(
            Pageable pageable,
            StudentFilterRequest filter) {

        Page<StudentResponse> responsePage = studentService.getStudents(pageable, filter);

        return ResponseEntity.ok(
                ApiResponse.<Page<StudentResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách sinh viên thành công")
                        .data(responsePage)
                        .build()
        );
    }

    // --- Tạo mới sinh viên ---
    @PostMapping
    @PreAuthorize("@ps.hasPermission('STUDENT_CREATE', null, null)")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {

        StudentResponse studentResponse = studentService.createStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StudentResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo sinh viên mới thành công")
                        .data(studentResponse)
                        .build()
        );
    }

    // --- Cập nhật thông tin sinh viên ---
    @PutMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('STUDENT_WRITE', null, null)")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable("id") String id,
            @Valid @RequestBody StudentUpdateRequest request) {

        StudentResponse studentResponse = studentService.updateStudent(id, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin sinh viên thành công")
                        .data(studentResponse)
                        .build()
        );
    }

    // --- Xóa sinh viên ---
    @DeleteMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('STUDENT_DELETE', null, null)")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable("id") String id) {
        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa sinh viên thành công")
                        .build()
        );
    }
}