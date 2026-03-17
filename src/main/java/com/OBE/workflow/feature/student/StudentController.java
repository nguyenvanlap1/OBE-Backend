package com.OBE.workflow.feature.student;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.student.request.StudentFilterRequest;
import com.OBE.workflow.feature.student.request.StudentRequest;
import com.OBE.workflow.feature.student.response.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    // --- Lấy danh sách Sinh viên (Phân trang + Lọc theo CTĐT) ---
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponse>>> getStudents(
            Pageable pageable,
            StudentFilterRequest filter) {

        Page<Student> studentPage = studentService.getStudents(pageable, filter);

        // Map sang DTO (Set<EducationProgram> -> Set<String> IDs)
        Page<StudentResponse> responsePage = studentPage.map(studentMapper::toResponse);

        return ResponseEntity.ok(
                ApiResponse.<Page<StudentResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách sinh viên thành công")
                        .data(responsePage)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        Student savedStudent = studentService.createStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StudentResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo sinh viên mới thành công")
                        .data(studentMapper.toResponse(savedStudent))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentRequest request) {

        Student updatedStudent = studentService.updateStudent(id, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin sinh viên thành công")
                        .data(studentMapper.toResponse(updatedStudent))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa sinh viên thành công")
                        .build()
        );
    }
}