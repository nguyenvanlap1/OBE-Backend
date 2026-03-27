package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.feature.course_section.grade.GradeRequest;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponse;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponseDetail;
import com.OBE.workflow.feature.course_section.request.CourseSectionCreateRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionFilterRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-sections")
@RequiredArgsConstructor
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

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
    /**
     * Lấy chi tiết lớp học phần (bao gồm cấu hình điểm và danh sách sinh viên)
     * GET /api/course-sections/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseSectionResponseDetail>> getCourseSectionDetail(
            @PathVariable("id") String id) {

        CourseSectionResponseDetail detail = courseSectionService.getCourseSectionDetail(id);

        return ResponseEntity.ok(
                ApiResponse.<CourseSectionResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Chi tiết lớp học phần [" + id + "]")
                        .data(detail)
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

    // --- ENDPOINTS QUẢN LÝ SINH VIÊN & ĐIỂM SỐ ---

    /**
     * Thêm sinh viên vào lớp học phần
     * POST /api/course-sections/{sectionId}/students/{studentId}
     */
    @PostMapping("/{sectionId}/students/{studentId}")
    public ResponseEntity<ApiResponse<Void>> addStudentToSection(
            @PathVariable String sectionId,
            @PathVariable String studentId) {

        courseSectionService.addStudentToSection(studentId, sectionId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã thêm sinh viên [" + studentId + "] vào lớp [" + sectionId + "]")
                        .build()
        );
    }

    /**
     * Xóa sinh viên khỏi lớp học phần
     * DELETE /api/course-sections/{sectionId}/students/{studentId}
     */
    @DeleteMapping("/{sectionId}/students/{studentId}")
    public ResponseEntity<ApiResponse<Void>> removeStudentFromSection(
            @PathVariable String sectionId,
            @PathVariable String studentId) {

        courseSectionService.removeStudentFromSection(studentId, sectionId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa sinh viên [" + studentId + "] khỏi lớp [" + sectionId + "]")
                        .build()
        );
    }

    /**
     * Cập nhật điểm cho một sinh viên cụ thể trong lớp
     * PUT /api/course-sections/{sectionId}/students/{studentId}/grades
     */
    @PutMapping("/{sectionId}/students/{studentId}/grades")
    public ResponseEntity<ApiResponse<Void>> updateStudentGrades(
            @PathVariable String sectionId,
            @PathVariable String studentId,
            @Valid @RequestBody List<GradeRequest> grades) {

        courseSectionService.updateStudentGrades(studentId, sectionId, grades);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật điểm cho sinh viên [" + studentId + "] thành công")
                        .build()
        );
    }

    /**
     * Đồng bộ khung điểm cho toàn bộ lớp (Dùng khi giảng viên thay đổi cấu hình CourseVersion)
     * POST /api/course-sections/{sectionId}/sync-grades
     */
    @PostMapping("/{sectionId}/sync-grades")
    public ResponseEntity<ApiResponse<Void>> syncAllGrades(@PathVariable String sectionId) {

        courseSectionService.syncAllGrades(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã đồng bộ khung điểm cho toàn bộ lớp [" + sectionId + "]")
                        .build()
        );
    }

    /**
     * Kiểm tra tính nhất quán của điểm số trong lớp
     * GET /api/course-sections/{sectionId}/validate-grades
     */
    @GetMapping("/{sectionId}/validate-grades")
    public ResponseEntity<ApiResponse<Void>> validateGrades(@PathVariable String sectionId) {

        courseSectionService.validateGrades(sectionId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Dữ liệu điểm của lớp [" + sectionId + "] hợp lệ và đồng nhất")
                        .build()
        );
    }
}