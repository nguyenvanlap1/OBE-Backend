package com.OBE.workflow.feature.school_year;

import com.OBE.workflow.conmon.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school-years")
@RequiredArgsConstructor
public class SchoolYearController {

    private final SchoolYearRepository schoolYearRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SchoolYear>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.<List<SchoolYear>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách khóa học thành công")
                        .data(schoolYearRepository.findAll())
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