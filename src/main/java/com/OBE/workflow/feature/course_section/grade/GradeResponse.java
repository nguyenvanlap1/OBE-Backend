package com.OBE.workflow.feature.course_section.grade;

import com.OBE.workflow.feature.course_version.response.CourseVersionResponseDetail;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse {
    private Long id;
    private Long sectionAssessmentCode;
    private Double score;

    public static GradeResponse fromEntity(Grade entity) {
        if (entity == null) return null;

        return GradeResponse.builder()
                .id(entity.getId())
                .sectionAssessmentCode(entity.getSectionAssessment().getSectionAssessmentCode())
                .score(entity.getScore())
                .build();
    }
}