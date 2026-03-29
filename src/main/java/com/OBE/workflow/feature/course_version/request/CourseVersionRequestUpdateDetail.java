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
public class CourseVersionRequestUpdateDetail {
    @NotBlank(message = "Thiếu mã học phần")
    private String courseId;
    @NotBlank(message="Thiếu mã bộ môn")
    private String subDepartmentId;
    @NotNull(message = "Thiếu số thứ tự phiên bản")
    private Integer versionNumber;
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
        private Long id;
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
        private Long id;
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
        private Long id;
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
        @NotNull(message = "Thiếu mã chuẩn đầu ra học phần")
        private String cloCode;
        @NotNull(message = "Thiếu trọng số trong ma trận ánh xạ assessment_clo")
        private Double weight;
    }

    public void validateInvariants() {
        // 1. Kiểm tra trùng mã CO
        if (cos != null) {
            Set<String> coCodes = new HashSet<>();
            for (CoRequest co : cos) {
                String code = co.getCoCode().trim().toUpperCase();
                if (!coCodes.add(code)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã CO: " + code);
                }
            }
        }

        // 2. Kiểm tra trùng mã CLO
        if (clos != null) {
            Set<String> cloCodes = new HashSet<>();
            for (CloRequest clo : clos) {
                String code = clo.getCloCode().trim().toUpperCase();
                if (!cloCodes.add(code)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã CLO: " + code);
                }
            }
        }

        // 3. Kiểm tra trùng mã Assessment
        if (assessments != null) {
            Set<Long> asmCodes = new HashSet<>();
            for (AssessmentRequest asm : assessments) {
                if (!asmCodes.add(asm.getAssessmentCode())) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã Assessment: " + asm.getAssessmentCode());
                }
            }
        }

        // 4. Kiểm tra Mapping (Tránh 1 cặp CO-CLO bị khai báo 2 lần trọng số)
        if (coCloMappings != null) {
            Set<String> pairs = new HashSet<>();
            for (var m : coCloMappings) {
                String pairKey = m.getCoCode().trim().toUpperCase() + "_" + m.getCloCode().trim().toUpperCase();
                if (!pairs.add(pairKey)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Mapping CO-CLO bị lặp: " + pairKey);
                }
            }
        }
    }
}

