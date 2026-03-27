package com.OBE.workflow.feature.course_section.grade;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse {
    private Long id;
    private String assessmentCode; // ID của thành phần đánh giá (Chuyên cần, Giữa kỳ...)
    private Double score;

    public static GradeResponse fromEntity(Grade entity) {
        if (entity == null) return null;

        return GradeResponse.builder()
                .id(entity.getId())
                .assessmentCode(entity.getAssessmentCode())
                .score(entity.getScore())
                .build();
    }
}