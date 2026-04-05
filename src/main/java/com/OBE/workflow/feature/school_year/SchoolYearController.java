package com.OBE.workflow.feature.school_year;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school-years")
@RequiredArgsConstructor
public class SchoolYearController {

    private final SchoolYearRepository schoolYearRepository;
    private final SchoolYearService schoolYearService;

    // 1. Tìm kiếm: Để tự do hoặc dùng quyền READ chung
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SchoolYear, SchoolYear>>> search(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable,
            @RequestBody(required = false) SchoolYear filter) {

        String idFilter = (filter != null) ? filter.getId() : null;
        Page<SchoolYear> page = schoolYearService.getSchoolYears(pageable, idFilter);
        List<SchoolYear> list = page.getContent();

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<SchoolYear, SchoolYear>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách niên khóa")
                        .data(PageResponse.fromPage(page, list))
                        .build()
        );
    }

    // 2. Thêm mới: Yêu cầu quyền tạo cấp Trường
    @PostMapping
    @PreAuthorize("@ps.hasPermission('SCHOOL_YEAR_CREATE', null, null)")
    public ResponseEntity<ApiResponse<SchoolYear>> create(@RequestBody SchoolYear schoolYear) {
        if(schoolYearRepository.existsById(schoolYear.getId())){
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Niên khóa đã tồn tại: " + schoolYear.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SchoolYear>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Thêm khóa học mới thành công")
                        .data(schoolYearRepository.save(schoolYear))
                        .build()
        );
    }

    // 3. Xóa: Yêu cầu quyền xóa cấp Trường
    @DeleteMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('SCHOOL_YEAR_DELETE', null, null)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") String id) {
        // Lưu ý: Nên bổ sung check existsById hoặc ràng buộc dữ liệu trước khi xóa
        schoolYearService.deleteSchoolYear(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Xóa khóa học thành công")
                        .build()
        );
    }
}