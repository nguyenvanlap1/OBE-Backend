package com.OBE.workflow.feature.education_program.request;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationProgramRequestUpdateDetail {

    private String id;
    private String name;
    private String educationLevel;
    private Integer requiredCredits;
    private String subDepartmentId;

    private List<String> schoolYearIds;
    // PO
    private List<PoRequest> pos;

    // PLO
    private List<PloRequest> plos;

    // Mapping
    private List<PloPoMappingRequest> ploPoMappings;

    // ================= PO =================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoRequest {
        private Long id;          // null = create mới
        private String poCode;
        private String content;
    }

    // ================= PLO =================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PloRequest {
        private Long id;
        private String ploCode;
        private String content;
    }

    // ================= MAPPING =================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PloPoMappingRequest {
        private String ploCode;
        private String poCode;
        private Double weight;
    }
}
