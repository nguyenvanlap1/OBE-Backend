package com.OBE.workflow.feature.course_version.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
        @NotBlank(message = "Thiếu mã đánh giá điểm thành phần")
        private String assessmentCode;
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
        @NotBlank(message = "Thiếu mã điểm thành phần")
        private String assessmentCode;
        @NotNull(message = "Thiếu mã chuẩn đầu ra học phần")
        private String cloCode;
        @NotNull(message = "Thiếu trọng số trong ma trận ánh xạ assessment_clo")
        private Double weight;
    }
}

