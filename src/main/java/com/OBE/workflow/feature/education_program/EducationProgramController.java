package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.education_program.request.EducationProgramFilterRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequestUpdateDetail;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponseDetail;
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

    @PostMapping("/create-detail")
    public ResponseEntity<ApiResponse<EducationProgramResponseDetail>> createProgramDetail(
            @Valid @RequestBody EducationProgramRequestUpdateDetail requestCreateDetail ) {
        EducationProgramResponseDetail educationProgramResponseDetail = educationProgramService.createProgramDetail(requestCreateDetail);
        return ResponseEntity.ok(
                ApiResponse.<EducationProgramResponseDetail>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo chi tiết chương trình đào tạo thành công")
                        .data(educationProgramResponseDetail)
                        .build()
        );
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

    /**
     * Endpoint lấy chi tiết chương trình đào tạo bao gồm PO, PLO và Mapping
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationProgramResponseDetail>> getProgramDetail(
            @PathVariable("id") String id) {

        EducationProgramResponseDetail detail = educationProgramService.getEducationProgramDetail(id);

        return ResponseEntity.ok(
                ApiResponse.<EducationProgramResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Thông tin chi tiết chương trình đào tạo")
                        .data(detail)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationProgramResponse>> updateProgram(
            @PathVariable("id") String id,
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
    public ResponseEntity<ApiResponse<Void>> deleteProgram(@PathVariable("id") String id) {
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

    @PutMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<EducationProgramResponseDetail>> updateProgramDetail(
            @PathVariable("id") String id,
            @Valid @RequestBody EducationProgramRequestUpdateDetail request) {

        // Gọi hàm logic phức tạp mà chúng ta vừa viết
        EducationProgramResponseDetail detail = educationProgramService.updateProgramDetail(id, request);

        return ResponseEntity.ok(
                ApiResponse.<EducationProgramResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật chi tiết PO, PLO và Mapping thành công")
                        .data(detail)
                        .build()
        );
    }
}