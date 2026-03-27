package com.OBE.workflow.feature.lecturer;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.lecturer.request.LecturerFilterRequest;
import com.OBE.workflow.feature.lecturer.request.LecturerRequest;
import com.OBE.workflow.feature.lecturer.response.LecturerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;
    private final LecturerMapper lecturerMapper;

    // --- Lấy danh sách có Phân trang và Lọc (Hỗ trợ lọc theo SubDepartment) ---
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LecturerResponse>>> getLecturers(
            Pageable pageable,
            LecturerFilterRequest filter) {

        Page<Lecturer> lecturerPage = lecturerService.getLecturers(pageable, filter);

        // Map Page<Entity> sang Page<Response> (đã có xử lý chuyển đổi IDs trong Mapper)
        Page<LecturerResponse> responsePage = lecturerPage.map(lecturerMapper::toResponse);

        return ResponseEntity.ok(
                ApiResponse.<Page<LecturerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách giảng viên thành công")
                        .data(responsePage)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LecturerResponse>> createLecturer(@Valid @RequestBody LecturerRequest request) {
        Lecturer savedLecturer = lecturerService.createLecturer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<LecturerResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo giảng viên mới thành công")
                        .data(lecturerMapper.toResponse(savedLecturer))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LecturerResponse>> updateLecturer(
            @PathVariable("id") String id,
            @Valid @RequestBody LecturerRequest request) {

        Lecturer updatedLecturer = lecturerService.updateLecturer(id, request);

        return ResponseEntity.ok(
                ApiResponse.<LecturerResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin giảng viên thành công")
                        .data(lecturerMapper.toResponse(updatedLecturer))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLecturer(@PathVariable("id") String id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa giảng viên thành công")
                        .build()
        );
    }
}