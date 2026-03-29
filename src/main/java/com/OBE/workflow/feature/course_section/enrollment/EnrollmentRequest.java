package com.OBE.workflow.feature.course_section.enrollment;

import com.OBE.workflow.feature.course_section.grade.GradeRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnrollmentRequest {
    @NotNull(message = "Thiếu mã để update điểm")
    private Long id;
    private List<GradeRequest> grades = new ArrayList<>();
}
