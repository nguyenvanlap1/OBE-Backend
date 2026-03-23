package com.OBE.workflow.feature.education_program.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationProgramResponseDetail {
    private String id;
    private String name;
    private String educationLevel;
    private Integer requiredCredits;
    private String subDepartmentId;
    private String subDepartmentName;

    private List<String> schoolYearIds;

    // Danh sách mục tiêu đào tạo (PO)
    private List<PoResponse> pos;

    // Danh sách chuẩn đầu ra chương trình (PLO)
    private List<PloResponse> plos;

    // Danh sách mapping giữa PLO và PO
    private List<PloPoMappingResponse> ploPoMappings;

    @Data
    @Builder
    public static class PoResponse {
        private Long id;
        private String poCode;
        private String content;
    }

    @Data
    @Builder
    public static class PloResponse {
        private Long id;
        private String ploCode;
        private String content;
    }

    @Data
    @Builder
    public static class PloPoMappingResponse {
        private Long ploId;
        private String ploCode;
        private Long poId;
        private String poCode;
        private Double weight;
    }
}