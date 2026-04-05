package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetailService;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetailResponse;
import com.OBE.workflow.feature.education_program.request.EducationProgramFilterRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequestUpdateDetail;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponseDetail;
import com.OBE.workflow.feature.education_program.response.ProgramCourseDetailListResponse;
import com.OBE.workflow.feature.sup_department.SubDepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/education-programs")
@RequiredArgsConstructor
public class EducationProgramController {

    private final EducationProgramService educationProgramService;
    private final ProgramCourseDetailService programCourseDetailService;
    private final EducationProgramMapper educationProgramMapper;
    private final SubDepartmentService subDepartmentService;

    @PostMapping
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_CREATE', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#request.id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#request.id))")
    public ResponseEntity<ApiResponse<EducationProgramResponse>> createProgram(
            @P("request")@Valid @RequestBody EducationProgramRequest request) {
        EducationProgram savedProgram = educationProgramService.createEducationProgram(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EducationProgramResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo chương trình đào tạo thành công")
                        .data(educationProgramMapper.toResponse(savedProgram))
                        .build());
    }

    @PostMapping("/create-detail")
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_CREATE', " +
            "#request.subDepartmentId, " +
            "@subDepartmentService.getDepartmentIdBySubDepartmentId(#request.subDepartmentId))")
    public ResponseEntity<ApiResponse<EducationProgramResponseDetail>> createProgramDetail(
            @P("request")@Valid @RequestBody EducationProgramRequestUpdateDetail requestCreateDetail ) {
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
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_UPDATE', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#id))")
    public ResponseEntity<ApiResponse<EducationProgramResponse>> updateProgram(
            @P("id") @PathVariable("id") String id,
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
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_DELETE', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#id))")
    public ResponseEntity<ApiResponse<Void>> deleteProgram(@P("id")  @PathVariable("id") String id) {
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
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_MANAGE_COURSES', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#id))")
    public ResponseEntity<ApiResponse<ProgramCourseDetailResponse>> addCourse(
            @P("id") @PathVariable("id") String programId,
            @RequestParam("courseId") String courseId,
            @RequestParam(value = "versionNumber", required = false) Integer versionNumber,
            @RequestParam(value = "knowledgeBlockId", required = false) String knowledgeBlockId) {

        // ĐỔI TẠI ĐÂY: Gọi Service chuyên biệt và truyền thêm KB ID
        ProgramCourseDetailResponse programCourseDetailResponse = programCourseDetailService.addCourseToProgram(programId, courseId, versionNumber, knowledgeBlockId);

        return ResponseEntity.ok(
                ApiResponse.<ProgramCourseDetailResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã thêm học phần vào chương trình đào tạo")
                        .data(programCourseDetailResponse)
                        .build()
        );
    }

    @DeleteMapping("/{id}/courses")
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_MANAGE_COURSES', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#id))")
    public ResponseEntity<ApiResponse<Void>> removeCourse(
            @P("id") @PathVariable("id") String programId,
            @RequestParam("courseId") String courseId) {

        // ĐỔI TẠI ĐÂY: Chỉ cần programId và courseId là đủ để xác định bản ghi cần xóa
        programCourseDetailService.removeCourseFromProgram(programId, courseId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa học phần khỏi chương trình đào tạo")
                        .build()
        );
    }

    @PutMapping("/{id}/detail")
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_UPDATE', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#id))")
    public ResponseEntity<ApiResponse<EducationProgramResponseDetail>> updateProgramDetail(
            @P("id") @PathVariable("id") String id,
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

    // --- Lấy danh sách học phần kèm khối kiến thức của một CTĐT ---
    @GetMapping("/{id}/courses")
    public ResponseEntity<ApiResponse<ProgramCourseDetailListResponse>> getProgramCourses(
            @PathVariable("id") String programId) {
        // Gọi Service trả về Object chứa List chi tiết
        ProgramCourseDetailListResponse data = programCourseDetailService.getCourseDetailsByProgramId(programId);
        return ResponseEntity.ok(
                ApiResponse.<ProgramCourseDetailListResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách học phần trong chương trình đào tạo")
                        .data(data)
                        .build()
        );
    }
    @PatchMapping("/{id}/courses/{courseId}/knowledge-block")
    @PreAuthorize("@ps.hasPermission('ED_PROGRAM_MANAGE_COURSES', " +
            "@educationProgramService.getSubDepartmentIdByProgramId(#id), " +
            "@educationProgramService.getDepartmentIdByProgramId(#id))")
    public ResponseEntity<ApiResponse<ProgramCourseDetailResponse>> updateCourseKnowledgeBlock(
            @P("id")@PathVariable("id") String programId,
            @PathVariable("courseId") String courseId,
            @RequestParam("knowledgeBlockId") String knowledgeBlockId) {

        ProgramCourseDetailResponse updated = programCourseDetailService.updateKnowledgeBlock(programId, courseId, knowledgeBlockId);

        return ResponseEntity.ok(
                ApiResponse.<ProgramCourseDetailResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật khối kiến thức thành công")
                        .data(updated)
                        .build()
        );
    }
}