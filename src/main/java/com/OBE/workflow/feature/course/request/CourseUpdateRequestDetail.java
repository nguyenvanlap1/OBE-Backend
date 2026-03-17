package com.OBE.workflow.feature.course.request;

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
public class CourseUpdateRequestDetail {
    private String courseId;
    private String defaultName;
    private Integer versionNumber;
    private Integer credits;
    private LocalDate fromDate;
    private LocalDate toDate;

    private List<CoRequest> cos;
    private List<CloRequest> clos;
    private List<AssessmentRequest> assessments;
    private List<CoCloMappingRequest> coCloMappings;
    private List<AssessmentCloMappingRequest> assessmentCloMappings;

    @Data
    public static class CoRequest {
        private Long id; // Gửi id lên để Update, null để Insert
        private String code;
        private String content;
    }

    @Data
    public static class CloRequest {
        private Long id; // Quan trọng để giữ liên kết với PLO
        private String code;
        private String content;
    }

    @Data
    public static class AssessmentRequest {
        private Long id;
        private String name;
        private String regulation;
        private Double weight;
    }

    @Data
    public static class CoCloMappingRequest {
        private Long coId;
        private Long cloId;
        private Double weight;
    }

    @Data
    public static class AssessmentCloMappingRequest {
        private Long assessmentId;
        private Long cloId;
        private Double weight;
    }
}