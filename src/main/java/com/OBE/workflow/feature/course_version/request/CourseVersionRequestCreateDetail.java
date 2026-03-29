package com.OBE.workflow.feature.course_version.request;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseVersionRequestCreateDetail {
    @NotBlank(message = "Thiếu mã học phần")
    private String courseId;
    @NotBlank(message = "Thiếu tên học phần")
    private String name;
    @NotNull(message = "Thiếu tín chỉ")
    private Integer credits;
    @NotNull(message = "Thiếu ngày áp dụng")
    private LocalDate fromDate;
    private LocalDate toDate;

    private List<CoRequest> cos;
    private List<CloRequest> clos;
    private List<AssessmentRequest> assessments;
    private List<CoCloMappingRequest> coCloMappings;
    private List<AssessmentCloMappingRequest> assessmentCloMappings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoRequest {
        @NotBlank(message = "Thiếu mã mục tiêu học phần")
        private String coCode;
        @NotBlank(message = "Thiếu nội dung mục tiêu học phần")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CloRequest {
        @NotBlank(message = "Thiếu mã chuẩn đầu ra học phần")
        private String cloCode;
        @NotBlank(message = "Thiếu nội dung chuẩn đầu ra học phần")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentRequest {
        @NotNull(message = "Thiếu mã đánh giá điểm thành phần")
        private Long assessmentCode;
        @NotBlank(message = "Thiếu tên điểm thành phần")
        private String name;
        @NotBlank(message="Thiếu quy định")
        private String regulation;
        @NotNull(message = "Thiếu trọng số điểm thành phần")
        private Double weight;     // Trọng số (ví dụ: 0.2 cho chuyên cần)
    }

    // Các class Mapping giữ nguyên vì đã đủ ID và Weight để vẽ Matrix
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoCloMappingRequest {
        @NotBlank(message = "Thiếu mã mục tiêu học phần")
        private String coCode;
        @NotBlank(message = "Thiếu mã chuẩn đầu ra học phần")
        private String cloCode;
        @NotNull(message = "Thiếu trọng số trong ma trận ánh xạ co_clo")
        private Double weight;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentCloMappingRequest {
        @NotNull(message = "Thiếu mã điểm thành phần")
        private Long assessmentCode;
        @NotBlank(message = "Thiếu mã chuẩn đầu ra học phần")
        private String cloCode;
        @NotNull(message = "Thiếu trọng số trong ma trận ánh xạ assessment_clo")
        private Double weight;
    }

    public void validateInvariants() {
        // --- 1. KIỂM TRA TRÙNG MÃ (UNIQUE CODES) ---
        Set<String> coCodes = new HashSet<>();
        if (cos != null) {
            for (var co : cos) {
                String code = co.getCoCode().trim().toUpperCase();
                if (!coCodes.add(code)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã CO trong danh sách: " + code);
                }
            }
        }

        Set<String> cloCodes = new HashSet<>();
        if (clos != null) {
            for (var clo : clos) {
                String code = clo.getCloCode().trim().toUpperCase();
                if (!cloCodes.add(code)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã CLO trong danh sách: " + code);
                }
            }
        }

        Set<Long> asmCodes = new HashSet<>();
        if (assessments != null) {
            double totalAsmWeight = 0;
            for (var asm : assessments) {
                if (!asmCodes.add(asm.getAssessmentCode())) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã Assessment trong danh sách: " + asm.getAssessmentCode());
                }
                totalAsmWeight += (asm.getWeight() != null ? asm.getWeight() : 0);
            }
            // Kiểm tra tổng trọng số Assessment (thường phải bằng 1.0 hoặc 100%)
            if (Math.abs(totalAsmWeight - 1.0) > 0.0001) {
                // Tùy nghiệp vụ bạn có muốn check cứng 100% ở đây không
                // throw new AppException(ErrorCode.INVALID_KEY, "Tổng trọng số Assessment phải bằng 1.0 (Hiện tại: " + totalAsmWeight + ")");
            }
        }

        // --- 2. KIỂM TRA TÍNH TOÀN VẸN MAPPING (REFERENTIAL INTEGRITY) ---
        if (coCloMappings != null) {
            Set<String> pairs = new HashSet<>();
            for (var m : coCloMappings) {
                String co = m.getCoCode().trim().toUpperCase();
                String clo = m.getCloCode().trim().toUpperCase();

                if (!coCodes.contains(co))
                    throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lỗi Mapping: Mã CO '" + co + "' không tồn tại trong danh sách CO");
                if (!cloCodes.contains(clo))
                    throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lỗi Mapping: Mã CLO '" + clo + "' không tồn tại trong danh sách CLO");

                if (!pairs.add(co + "_" + clo)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Lặp Mapping CO-CLO cho cặp: " + co + "-" + clo);
                }
            }
        }

        if (assessmentCloMappings != null) {
            Set<String> pairs = new HashSet<>();
            for (var m : assessmentCloMappings) {
                Long asm = m.getAssessmentCode();
                String clo = m.getCloCode().trim().toUpperCase();

                if (!asmCodes.contains(asm))
                    throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lỗi Mapping: Mã Assessment '" + asm + "' không tồn tại");
                if (!cloCodes.contains(clo))
                    throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lỗi Mapping: Mã CLO '" + clo + "' không tồn tại");

                if (!pairs.add(asm + "_" + clo)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Lặp Mapping Assessment-CLO cho cặp: " + asm + "-" + clo);
                }
            }
        }
    }
}
