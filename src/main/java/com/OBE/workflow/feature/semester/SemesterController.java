package com.OBE.workflow.feature.semester;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;

    @PostMapping
    public ResponseEntity<ApiResponse<SemesterResponse>> createSemester(@Valid @RequestBody SemesterRequest request) {
        Semester savedSemester = semesterService.createSemester(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SemesterResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo học kỳ mới thành công")
                        .data(SemesterResponse.fromEntity(savedSemester))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterResponse>> updateSemester(
            @PathVariable("id") Long id,
            @Valid @RequestBody SemesterRequest request) {

        Semester updatedSemester = semesterService.updateSemester(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SemesterResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin học kỳ thành công")
                        .data(SemesterResponse.fromEntity(updatedSemester))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSemester(@PathVariable("id") Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa học kỳ thành công")
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<Semester, SemesterResponse>>> getSemesters(
            @org.springdoc.core.annotations.ParameterObject org.springframework.data.domain.Pageable pageable,
                 @RequestBody   SemesterFilterRequest filterRequest ) {

        // Lấy dữ liệu phân trang từ Service
        Page<Semester> page = semesterService.getSemesters(pageable, filterRequest);

        // Map danh sách Entity sang Response DTO
        List<SemesterResponse> responseList = page.getContent().stream()
                .map(SemesterResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<Semester, SemesterResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách học kỳ")
                        .data(PageResponse.fromPage(page, responseList))
                        .build()
        );
    }
}