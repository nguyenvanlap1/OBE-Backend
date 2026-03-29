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
public class CourseVersionRequestCreateFirstDetail {
    @NotBlank(message = "Thiếu mã học phần")
    private String courseId;
    @NotBlank(message = "Thiếu tên học phần")
    private String name;
    @NotBlank(message = "Thiếu mã bộ môn")
    private String subDepartmentId;
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
        // 1. Kiểm tra trùng mã và thu thập danh sách code để đối chiếu mapping
        Set<String> coCodes = new HashSet<>();
        if (cos != null) {
            for (var co : cos) {
                String code = co.getCoCode().trim().toUpperCase();
                if (!coCodes.add(code)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã CO: " + code);
                }
            }
        }

        Set<String> cloCodes = new HashSet<>();
        if (clos != null) {
            for (var clo : clos) {
                String code = clo.getCloCode().trim().toUpperCase();
                if (!cloCodes.add(code)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã CLO: " + code);
                }
            }
        }

        Set<Long> asmCodes = new HashSet<>();
        if (assessments != null) {
            for (var asm : assessments) {
                if (!asmCodes.add(asm.getAssessmentCode())) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Trùng mã Assessment: " + asm.getAssessmentCode());
                }
            }
        }

        // 2. Kiểm tra tính hợp lệ của Mapping CO-CLO
        if (coCloMappings != null) {
            Set<String> pairs = new HashSet<>();
            for (var m : coCloMappings) {
                String co = m.getCoCode().trim().toUpperCase();
                String clo = m.getCloCode().trim().toUpperCase();

                // Kiểm tra xem mã trong mapping có tồn tại trong danh sách khai báo không
                if (!coCodes.contains(co)) throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CO trong mapping không tồn tại: " + co);
                if (!cloCodes.contains(clo)) throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CLO trong mapping không tồn tại: " + clo);

                if (!pairs.add(co + "_" + clo)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Mapping CO-CLO bị lặp: " + co + "-" + clo);
                }
            }
        }

        // 3. Kiểm tra tính hợp lệ của Mapping Assessment-CLO
        if (assessmentCloMappings != null) {
            Set<String> pairs = new HashSet<>();
            for (var m : assessmentCloMappings) {
                Long asm = m.getAssessmentCode();
                String clo = m.getCloCode().trim().toUpperCase();

                if (!asmCodes.contains(asm)) throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã Assessment trong mapping không tồn tại: " + asm);
                if (!cloCodes.contains(clo)) throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CLO trong mapping không tồn tại: " + clo);

                if (!pairs.add(asm + "_" + clo)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Mapping Assessment-CLO bị lặp: " + asm + "-" + clo);
                }
            }
        }
    }
}