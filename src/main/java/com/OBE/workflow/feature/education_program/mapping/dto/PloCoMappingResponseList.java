package com.OBE.workflow.feature.education_program.mapping.dto;

import com.OBE.workflow.feature.education_program.mapping.PloCoMapping;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetail;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PloCoMappingResponseList {
    private String educationProgramId;
    private String educationProgramName;
    private String courseId;
    private String courseName;
    private Integer versionNumber;
    private List<PloCoMappingResponse> mappings;

    // Chỉ cần nhận vào List Entity Mapping
    public static PloCoMappingResponseList fromEntities(List<PloCoMapping> entities) {
        if (entities == null || entities.isEmpty()) return null;

        // Lấy đại diện một mapping để trích xuất thông tin chung (Context)
        PloCoMapping first = entities.getFirst();
        ProgramCourseDetail detail = first.getProgramCourseDetail();

        return PloCoMappingResponseList.builder()
                .educationProgramId(detail.getEducationProgram().getId())
                .educationProgramName(detail.getEducationProgram().getName())
                .courseId(detail.getCourseVersion().getCourse().getId())
                .courseName(detail.getCourseVersion().getName())
                .versionNumber(detail.getCourseVersion().getVersionNumber())
                .mappings(entities.stream()
                        .map(PloCoMappingResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}