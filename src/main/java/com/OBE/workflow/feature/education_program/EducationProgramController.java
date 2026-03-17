package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.education_program.request.EducationProgramFilterRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/education-programs")
@RequiredArgsConstructor
public class EducationProgramController {

    private final EducationProgramService educationProgramService;
    private final EducationProgramMapper educationProgramMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<EducationProgramResponse>> createProgram(
            @Valid @RequestBody EducationProgramRequest request) {
        EducationProgram savedProgram = educationProgramService.createEducationProgram(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EducationProgramResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo chương trình đào tạo thành công")
                        .data(educationProgramMapper.toResponse(savedProgram))
                        .build());
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<EducationProgramResponse, EducationProgramResponse>>> getPrograms(
            @ParameterObject Pageable pageable,
            @RequestBody EducationProgramFilterRequest filterRequest) {

        // Service trả về Page<EducationProgramResponse>
        Page<EducationProgramResponse> page = educationProgramService.getEducationPrograms(pageable, filterRequest);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<EducationProgramResponse, EducationProgramResponse>>builder()
                        .status(HttpStatus.OK.value()) // Thêm status cho giống mẫu Department
                        .message("Danh sách chương trình đào tạo")
                        .data(PageResponse.fromPage(page))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationProgramResponse>> updateProgram(
            @PathVariable String id,
            @Valid @RequestBody EducationProgramRequest request) {
        request.setId(id); // Đồng bộ ID từ PathVariable vào Request
        EducationProgramResponse updated = educationProgramService.updateEducationProgram(request);

        return ResponseEntity.ok(
                ApiResponse.<EducationProgramResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật chương trình đào tạo thành công")
                        .data(updated)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProgram(@PathVariable String id) {
        educationProgramService.deleteEducationProgram(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa chương trình đào tạo thành công")
                        .build()
        );
    }

    // --- Endpoint mở rộng: Quản lý học phần trong chương trình ---
    @PostMapping("/{id}/courses")
    public ResponseEntity<ApiResponse<Void>> addCourse(
            @PathVariable("id") String programId,
            @RequestParam("courseId") String courseId,
            @RequestParam(value = "versionNumber", required = false) Integer versionNumber) {

        educationProgramService.addCourseToProgram(programId, courseId, versionNumber);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã thêm học phần vào chương trình đào tạo")
                        .build()
        );
    }

    @DeleteMapping("/{id}/courses")
    public ResponseEntity<ApiResponse<Void>> removeCourse(
            @PathVariable("id") String programId,
            @RequestParam("courseId") String courseId,
            @RequestParam("versionNumber") Integer versionNumber) {

        educationProgramService.removeCourseFromProgram(programId, courseId, versionNumber);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa học phần khỏi chương trình đào tạo")
                        .build()
        );
    }
}