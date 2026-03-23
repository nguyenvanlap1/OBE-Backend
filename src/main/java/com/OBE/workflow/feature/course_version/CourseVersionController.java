package com.OBE.workflow.feature.course_version;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.feature.course_version.request.CourseVersionFilterRequest;
import com.OBE.workflow.feature.course_version.request.CourseVersionRequestCreateDetail;
import com.OBE.workflow.feature.course_version.request.CourseVersionRequestCreateFirstDetail;
import com.OBE.workflow.feature.course_version.request.CourseVersionRequestUpdateDetail;
import com.OBE.workflow.feature.course_version.response.CourseVersionResponse;
import com.OBE.workflow.feature.course_version.response.CourseVersionResponseDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-versions")
@RequiredArgsConstructor
public class CourseVersionController {

    private final CourseVersionService courseVersionService;

    /**
     * Tìm kiếm và phân trang danh sách phiên bản học phần
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CourseVersionResponse, CourseVersionResponse>>> getCourseVersions(
            @ParameterObject Pageable pageable,
            @RequestBody CourseVersionFilterRequest filterRequest) {

        Page<CourseVersionResponse> page = courseVersionService.getCourseVersions(pageable, filterRequest);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<CourseVersionResponse, CourseVersionResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách phiên bản học phần")
                        .data(PageResponse.fromPage(page))
                        .build()
        );
    }

    /**
     * Lấy chi tiết một phiên bản cụ thể
     */
    @GetMapping("/{courseId}/{versionNumber}")
    public ResponseEntity<ApiResponse<CourseVersionResponseDetail>> getCourseVersionDetail(
            @PathVariable("courseId") String courseId,
            @PathVariable("versionNumber") Integer versionNumber) {

        CourseVersionResponseDetail detail = courseVersionService.getCourseVersionDetail(courseId, versionNumber);

        return ResponseEntity.ok(
                ApiResponse.<CourseVersionResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Chi tiết phiên bản học phần")
                        .data(detail)
                        .build()
        );
    }

    /**
     * Tạo mới học phần và phiên bản đầu tiên (V1)
     */
    @PostMapping("/first")
    public ResponseEntity<ApiResponse<CourseVersionResponseDetail>> createFirstVersion(
            @Valid @RequestBody CourseVersionRequestCreateFirstDetail request) {

        CourseVersionResponseDetail detail = courseVersionService.createFirstCourseVersionDetail(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CourseVersionResponseDetail>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo mới học phần và phiên bản đầu tiên thành công")
                        .data(detail)
                        .build()
        );
    }

    /**
     * Tạo phiên bản kế tiếp cho học phần đã tồn tại (V+1)
     */
    @PostMapping("/next")
    public ResponseEntity<ApiResponse<CourseVersionResponseDetail>> createNextVersion(
            @Valid @RequestBody CourseVersionRequestCreateDetail request) {

        CourseVersionResponseDetail detail = courseVersionService.createNextCourseVersionDetail(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CourseVersionResponseDetail>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo phiên bản mới thành công")
                        .data(detail)
                        .build()
        );
    }

    /**
     * Cập nhật thông tin chi tiết của một phiên bản (bao gồm CO, CLO, Assessment và Mapping)
     */
    @PutMapping
    public ResponseEntity<ApiResponse<CourseVersionResponseDetail>> updateVersionDetail(
            @Valid @RequestBody CourseVersionRequestUpdateDetail request) {

        CourseVersionResponseDetail detail = courseVersionService.updateCourseVersionDetail(request);

        return ResponseEntity.ok(
                ApiResponse.<CourseVersionResponseDetail>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật chi tiết phiên bản thành công")
                        .data(detail)
                        .build()
        );
    }

    /**
     * Xóa một phiên bản học phần
     */
    @DeleteMapping("/{courseId}/{versionNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteVersion(
            @PathVariable("courseId") String courseId,
            @PathVariable("versionNumber") Integer versionNumber) {

        courseVersionService.deleteCourseVersion(courseId, versionNumber);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa phiên bản học phần thành công")
                        .build()
        );
    }
}
