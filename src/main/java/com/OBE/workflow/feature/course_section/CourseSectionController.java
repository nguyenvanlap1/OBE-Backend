package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentRequest;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentResponse;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionGradeResponse;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponse;
import com.OBE.workflow.feature.course_section.request.CourseSectionCreateRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionFilterRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-sections")
@RequiredArgsConstructor
@Slf4j
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    // --- QUẢN LÝ LỚP HỌC PHẦN (CRUD) ---

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CourseSectionResponse, CourseSectionResponse>>> getCourseSections(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable,
            @RequestBody CourseSectionFilterRequest filter) {

        Page<CourseSectionResponse> pageResponse = courseSectionService.getCourseSections(pageable, filter);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<CourseSectionResponse, CourseSectionResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách lớp học phần")
                        .data(PageResponse.fromPage(pageResponse, pageResponse.getContent()))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseSectionResponse>> getCourseSectionDetail(
            @PathVariable("id") String id) {

        CourseSectionResponse detail = courseSectionService.getCourseSection(id);

        return ResponseEntity.ok(
                ApiResponse.<CourseSectionResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Chi tiết điểm lớp học phần [" + id + "]")
                        .data(detail)
                        .build()
        );
    }

    @GetMapping("/{id}/grades")
    public ResponseEntity<ApiResponse<CourseSectionGradeResponse>> getCourseGradeResponse(
            @PathVariable("id") String id){
        CourseSectionGradeResponse response = courseSectionService.getCourseGradeResponse(id);

        return ResponseEntity.ok(
                ApiResponse.<CourseSectionGradeResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Thông tin lớp học phần [" + id + "]")
                        .data(response)
                        .build()
        );
    }


    @PostMapping
    public ResponseEntity<ApiResponse<CourseSectionResponse>> createCourseSection(
            @Valid @RequestBody CourseSectionCreateRequest request) {

        CourseSectionResponse savedSection = courseSectionService.createCourseSection(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CourseSectionResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo lớp học phần mới thành công")
                        .data(savedSection)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseSectionResponse>> updateCourseSection(
            @PathVariable("id") String id,
            @Valid @RequestBody CourseSectionUpdateRequest request) {

        CourseSectionResponse updatedSection = courseSectionService.updateCourseSection(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CourseSectionResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin lớp học phần [" + id + "] thành công")
                        .data(updatedSection)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseSection(@PathVariable("id") String id) {
        courseSectionService.deleteCourseSection(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa lớp học phần [" + id + "] thành công")
                        .build()
        );
    }

    // --- QUẢN LÝ SINH VIÊN TRONG LỚP ---

    @PostMapping("/{sectionId}/students/{studentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> addStudentToSection(
            @PathVariable("sectionId") String sectionId,
            @PathVariable("studentId") String studentId) {

        EnrollmentResponse enrollmentResponse = courseSectionService.addStudentToSection(studentId, sectionId);
        return ResponseEntity.ok(
                ApiResponse.<EnrollmentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã thêm sinh viên [" + studentId + "] vào lớp [" + sectionId + "]")
                        .data(enrollmentResponse)
                        .build()
        );
    }

    @DeleteMapping("/{sectionId}/students/{studentId}")
    public ResponseEntity<ApiResponse<Void>> removeStudentFromSection(
            @PathVariable("sectionId") String sectionId,
            @PathVariable("studentId") String studentId) {

        courseSectionService.removeStudentFromSection(studentId, sectionId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa sinh viên [" + studentId + "] khỏi lớp [" + sectionId + "]")
                        .build()
        );
    }

    /**
     * Cập nhật danh sách nhiều đầu điểm cùng lúc cho 1 sinh viên (Batch)
     */
    @PutMapping("/{sectionId}/students/{enrollmentId}/grades-batch")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateStudentGradesBatch(
            @PathVariable String enrollmentId,
            @Valid @RequestBody EnrollmentRequest request) {

        EnrollmentResponse enrollmentResponse =  courseSectionService.updateStudentGrade(request);

        return ResponseEntity.ok(
                ApiResponse.<EnrollmentResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật danh sách điểm thành công")
                        .data(enrollmentResponse)
                        .build()
        );
    }

    /**
     * Cập nhật một đầu điểm đơn lẻ (Phục vụ AG Grid Edit)
     * URL: PATCH /api/course-sections/enrollments/{enrollmentId}/grades/{saCode}?score=9.5
     */
    @PatchMapping("/enrollments/{enrollmentId}/grades/{saCode}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateSingleStudentGrade(
            @PathVariable("enrollmentId") Long enrollmentId, // Thêm "enrollmentId"
            @PathVariable("saCode") Long saCode,             // Thêm "saCode"
            @RequestParam("score") Double score) {

        // Log đầu vào để kiểm tra giá trị từ Frontend gửi lên
        log.info("Yêu cầu cập nhật điểm: enrollmentId={}, saCode={}, score={}", enrollmentId, saCode, score);

        try {
            EnrollmentResponse response = courseSectionService.updateSingleStudentGrade(enrollmentId, saCode, score);
            log.info("Cập nhật điểm thành công cho enrollmentId: {}", enrollmentId);

            return ResponseEntity.ok(
                    ApiResponse.<EnrollmentResponse>builder()
                            .status(HttpStatus.OK.value())
                            .message("Cập nhật điểm thành công")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            // Log lỗi chi tiết kèm theo StackTrace để biết chính xác dòng nào bị lỗi
            log.error("Lỗi khi cập nhật điểm cho enrollmentId {}: {}", enrollmentId, e.getMessage(), e);
            throw e; // Throw để GlobalExceptionHandler hoặc Spring xử lý trả về 500/400
        }
    }
}