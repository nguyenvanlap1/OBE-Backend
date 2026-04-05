package com.OBE.workflow.feature.education_program.response;

import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.school_year.SchoolYear;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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

    public static EducationProgramResponseDetail fromEntity(EducationProgram entity) {
        if (entity == null) return null;

        return EducationProgramResponseDetail.builder()
                .id(entity.getId())
                .name(entity.getName())
                .educationLevel(entity.getEducationLevel())
                .requiredCredits(entity.getRequiredCredits())
                .subDepartmentId(entity.getSubDepartment().getId())
                .subDepartmentName(entity.getSubDepartment().getName())
                .schoolYearIds(entity.getSchoolYears() != null
                        ? entity.getSchoolYears().stream().map(SchoolYear::getId).toList()
                        : null)
                .pos(entity.getPos() != null
                        ? entity.getPos().stream().map(po -> PoResponse.builder()
                        .id(po.getId())
                        .poCode(po.getPoCode())
                        .content(po.getContent())
                        .build()).toList()
                        : null)
                .plos(entity.getPlos() != null
                        ? entity.getPlos().stream().map(plo -> PloResponse.builder()
                        .id(plo.getId())
                        .ploCode(plo.getPloCode())
                        .content(plo.getContent())
                        .build()).toList()
                        : null)
                // Xử lý Mapping thông qua bảng trung gian nằm trong PLO
                .ploPoMappings(entity.getPlos() != null
                        ? entity.getPlos().stream()
                        .filter(plo -> plo.getMappings() != null)
                        .flatMap(plo -> plo.getMappings().stream().map(mapping -> {
                            var po = mapping.getPo();
                            return PloPoMappingResponse.builder()
                                    .ploId(plo.getId())
                                    .ploCode(plo.getPloCode())
                                    .poId(po != null ? po.getId() : null)
                                    .poCode(po != null ? po.getPoCode() : null)
                                    .weight(mapping.getWeight())
                                    .build();
                        }))
                        .toList()
                        : null)
                .build();
    }
}