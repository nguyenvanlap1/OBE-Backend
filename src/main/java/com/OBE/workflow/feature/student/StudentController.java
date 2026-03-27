package com.OBE.workflow.feature.student;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // 1. Tìm kiếm và phân trang sinh viên (Sử dụng POST để nhận Body Filter)
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse, StudentResponse>>> searchStudents(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable,
            @RequestBody StudentFilterRequest filter) {

        // Lấy dữ liệu phân trang từ Service
        Page<StudentResponse> pageResponse = studentService.getStudents(pageable, filter);

        // Map danh sách từ Page (Vì Service của bạn đã trả về StudentResponse nên lấy trực tiếp content)
        List<StudentResponse> responseList = pageResponse.getContent();

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<StudentResponse, StudentResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách sinh viên thành công")
                        .data(PageResponse.fromPage(pageResponse, responseList))
                        .build()
        );
    }

    // 2. Tạo mới sinh viên
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {

        StudentResponse savedStudent = studentService.createStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StudentResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo sinh viên mới thành công")
                        .data(savedStudent)
                        .build()
        );
    }

    // 3. Cập nhật thông tin sinh viên
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable("id") String id,
            @Valid @RequestBody StudentUpdateRequest request) {

        StudentResponse updatedStudent = studentService.updateStudent(id, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin sinh viên thành công")
                        .data(updatedStudent)
                        .build()
        );
    }

    // 4. Xóa sinh viên
    @DeleteMapping("/{id}")
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