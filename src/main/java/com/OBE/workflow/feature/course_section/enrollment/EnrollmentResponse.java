package com.OBE.workflow.feature.course_section.enrollment;

import com.OBE.workflow.feature.course_section.grade.GradeResponse; // Giả định bạn đã có DTO này
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnrollmentResponse {
    private Long id;
    private String studentId;
    private String studentFullName;

    @Builder.Default
    private List<GradeResponse> grades = new ArrayList<>();
    public static EnrollmentResponse fromEntity(Enrollment entity) {
        if (entity == null) return null;

        return EnrollmentResponse.builder()
                .id(entity.getId())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .studentFullName(entity.getStudent() != null ? entity.getStudent().getFullName() : null)
                .grades(entity.getGrades() != null
                        ? entity.getGrades().stream()
                        .map(GradeResponse::fromEntity)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}