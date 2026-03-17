package com.OBE.workflow.feature.course;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.feature.course.request.CourseCreateRequest;
import com.OBE.workflow.feature.course.request.CourseFilterRequest;
import com.OBE.workflow.feature.course.request.CourseUpdateRequest;
import com.OBE.workflow.feature.course.request.CourseUpdateRequestDetail;
import com.OBE.workflow.feature.course.response.CourseResponse;
import com.OBE.workflow.feature.course.response.CourseResponseDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse savedCourse = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CourseResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Đã tạo học phần thành công")
                .data(savedCourse)
                .build());
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<?, CourseResponse>>> getCourses(
            @ParameterObject Pageable pageable,
            @RequestBody CourseFilterRequest filter) {

        Page<CourseResponse> page = courseService.getCourses(pageable, filter);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<?, CourseResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách học phần")
                        .data(PageResponse.fromPage(page, page.getContent()))
                        .build()
        );
    }

    @GetMapping("/{id}/versions/{versionNumber}/detail")
    public ResponseEntity<ApiResponse<CourseResponseDetail>> getCourseDetail(
            @PathVariable("id") String id,
            @PathVariable("versionNumber") Integer versionNumber) {

        CourseResponseDetail detail = courseService.getCourseDetail(id, versionNumber);

        return ResponseEntity.ok(
                ApiResponse.<CourseResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Chi tiết học phần và ma trận ánh xạ")
                        .data(detail)
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @Valid @RequestBody CourseUpdateRequest request) {
        CourseResponse updatedCourse = courseService.updateCourse(request);
        return ResponseEntity.ok(
                ApiResponse.<CourseResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật học phần thành công")
                        .data(updatedCourse)
                        .build()
        );
    }

    @PutMapping("/detail")
    public ResponseEntity<ApiResponse<CourseResponseDetail>> updateCourseDetail(
            @Valid @RequestBody CourseUpdateRequestDetail request) {

        CourseResponseDetail updatedDetail = courseService.updateCourseVersionDetail(request);

        return ResponseEntity.ok(
                ApiResponse.<CourseResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật chi tiết đề cương học phần thành công")
                        .data(updatedDetail)
                        .build()
        );
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllVersions(@PathVariable String id) {
        List<CourseResponse> versions = courseService.getAllVersions(id);
        return ResponseEntity.ok(
                ApiResponse.<List<CourseResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lịch sử phiên bản học phần")
                        .data(versions)
                        .build()
        );
    }

    @DeleteMapping("/{id}/versions/{versionNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteVersion(
            @PathVariable String id,
            @PathVariable Integer versionNumber) {
        courseService.deleteCourseVersion(id, versionNumber);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa phiên bản học phần thành công")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFullCourse(@PathVariable String id) {
        courseService.deleteFullCourse(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa hoàn toàn học phần")
                        .build()
        );
    }
}