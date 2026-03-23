package com.OBE.workflow.feature.assessment_score.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentScoreRequest {

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentId;

    @NotNull(message = "ID lớp học phần không được để trống")
    private Long courseSectionId;

    @NotNull(message = "ID thành phần điểm không được để trống")
    private Long assessmentId;

    @NotNull(message = "Điểm số không được để trống")
    @Min(value = 0, message = "Điểm không được nhỏ hơn 0")
    @Max(value = 10, message = "Điểm không được lớn hơn 10")
    private Double score;
}