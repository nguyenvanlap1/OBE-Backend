package com.OBE.workflow.feature.education_program.mapping;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.education_program.mapping.dto.EducationProgramCourseSummaryList;
import com.OBE.workflow.feature.education_program.mapping.dto.PloCoMappingRequest;
import com.OBE.workflow.feature.education_program.mapping.dto.PloCoMappingResponse;
import com.OBE.workflow.feature.education_program.mapping.dto.PloCoMappingResponseList;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/education-programs/{programId}/courses/{courseId}/plo-co-mappings")
@RequiredArgsConstructor
public class PloCoMappingController {

    private final PloCoMappingService ploCoMappingService;

    @GetMapping
    public ResponseEntity<ApiResponse<PloCoMappingResponseList>> getMappings(
            @PathVariable("programId") String programId,
            @PathVariable("courseId") String courseId) {

        PloCoMappingResponseList ploCoMappingResponseList = ploCoMappingService.getListMapping(programId, courseId);

        return ResponseEntity.ok(
                ApiResponse.<PloCoMappingResponseList>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách ánh xạ PLO-CO thành công")
                        .data(ploCoMappingResponseList)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PloCoMappingResponse>> upsertMapping(
            @PathVariable("programId") String programId,
            @PathVariable("courseId") String courseId,
            @Valid @RequestBody PloCoMappingRequest request) {

        PloCoMappingResponse result = ploCoMappingService.upsertMapping(
                programId,
                courseId,
                request.getPloCode(),
                request.getCoCode(),
                request.getWeight()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PloCoMappingResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Cập nhật ánh xạ thành công")
                        .data(result)
                        .build()
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeMapping(
            @PathVariable("programId") String programId,
            @PathVariable("courseId") String courseId,
            @RequestParam("ploCode") String ploCode,
            @RequestParam("coCode") String coCode) {

        ploCoMappingService.removeMapping(programId, courseId, ploCode, coCode);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa ánh xạ thành công")
                        .build()
        );
    }

    // Thêm hàm này vào PloCoMappingController

    /**
     * Tra cứu các Chương trình đào tạo mà một học phần (kèm phiên bản) đang tham gia.
     * Path: /api/education-programs/course-summary/{courseId}/{version}
     */
    @GetMapping("/course-summary/{version}")
    public ResponseEntity<ApiResponse<EducationProgramCourseSummaryList>> getEducationProgramCourseSummary(
            @PathVariable("courseId") String courseId,
            @PathVariable("version") Integer version) {

        EducationProgramCourseSummaryList summary = ploCoMappingService.getEducationProgramCourseSummaryList(courseId, version);

        return ResponseEntity.ok(
                ApiResponse.<EducationProgramCourseSummaryList>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách chương trình đào tạo của học phần thành công")
                        .data(summary)
                        .build()
        );
    }
}