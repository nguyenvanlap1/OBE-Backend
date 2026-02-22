package com.OBE.workflow.feature.officer;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.officer.request.OfficerRequest;
import com.OBE.workflow.feature.officer.response.OfficerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officers")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;
    private final OfficerMapper officerMapper;

    // --- Lấy danh sách có Phân trang và Lọc ---
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OfficerResponse>>> getOfficers(
            Pageable pageable,
            OfficerRequest filter) {

        Page<Officer> officerPage = officerService.getOfficers(pageable, filter);

        // Map Page<Entity> sang Page<Response>
        Page<OfficerResponse> responsePage = officerPage.map(officerMapper::toResponse);

        return ResponseEntity.ok(
                ApiResponse.<Page<OfficerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách cán bộ thành công")
                        .data(responsePage)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OfficerResponse>> createOfficer(@Valid @RequestBody OfficerRequest request) {
        Officer savedOfficer = officerService.createOfficer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OfficerResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Đã tạo cán bộ/giảng viên mới thành công")
                        .data(officerMapper.toResponse(savedOfficer))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OfficerResponse>> updateOfficer(
            @PathVariable String id,
            @Valid @RequestBody OfficerRequest request) {

        Officer updatedOfficer = officerService.updateOfficer(id, request);

        return ResponseEntity.ok(
                ApiResponse.<OfficerResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thông tin cán bộ thành công")
                        .data(officerMapper.toResponse(updatedOfficer))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOfficer(@PathVariable String id) {
        officerService.deleteOfficer(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xóa cán bộ thành công")
                        .build()
        );
    }
}