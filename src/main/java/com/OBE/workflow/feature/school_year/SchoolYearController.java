package com.OBE.workflow.feature.school_year;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school-years")
@RequiredArgsConstructor
public class SchoolYearController {

    private final SchoolYearRepository schoolYearRepository;
    private final SchoolYearService schoolYearService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SchoolYear, SchoolYear>>> search(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable,
            @RequestBody(required = false) SchoolYear filter) {

        // 1. Lấy dữ liệu phân trang (filter có thể lấy id từ object SchoolYear)
        String idFilter = (filter != null) ? filter.getId() : null;
        Page<SchoolYear> page = schoolYearService.getSchoolYears(pageable, idFilter);

        // 2. Vì SchoolYear Entity và Response giống nhau nên không cần Mapper phức tạp
        List<SchoolYear> list = page.getContent();

        // 3. Trả về format chuẩn
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<SchoolYear, SchoolYear>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Danh sách niên khóa")
                        .data(PageResponse.fromPage(page, list))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SchoolYear>> create(@RequestBody SchoolYear schoolYear) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SchoolYear>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Thêm khóa học mới thành công")
                        .data(schoolYearRepository.save(schoolYear))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        schoolYearRepository.deleteById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Xóa khóa học thành công")
                        .build()
        );
    }
}