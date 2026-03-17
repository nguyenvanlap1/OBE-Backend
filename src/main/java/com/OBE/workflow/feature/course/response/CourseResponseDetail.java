package com.OBE.workflow.feature.course.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDetail {
    private String courseId;
    private String defaultName;
    private String subDepartmentName;
    private Integer versionNumber;
    private Integer credits;
    private LocalDate fromDate;
    private LocalDate toDate;

    private List<CoResponse> cos;
    private List<CloResponse> clos;
    private List<AssessmentResponse> assessments;
    private List<CoCloMappingResponse> coCloMappings;
    private List<AssessmentCloMappingResponse> assessmentCloMappings;

    @Data
    @Builder
    public static class CoResponse {
        private Long id;
        private String code;
        private String content; // Cập nhật từ description -> content
    }

    @Data
    @Builder
    public static class CloResponse {
        private Long id;
        private String code;
        private String content; // Cập nhật từ description -> content
    }

    @Data
    @Builder
    public static class AssessmentResponse {
        private Long id;
        private String name;
        private String regulation; // Bổ sung trường này
        private Double weight;
    }

    @Data
    @Builder
    public static class CoCloMappingResponse {
        private Long coId;
        private Long cloId;
        private Double weight;
    }

    @Data
    @Builder
    public static class AssessmentCloMappingResponse {
        private Long assessmentId;
        private Long cloId;
        private Double weight;
    }
}