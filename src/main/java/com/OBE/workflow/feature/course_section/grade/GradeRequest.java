package com.OBE.workflow.feature.course_section.grade;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRequest {
    @NotNull(message = "Thiếu mã thành phần đánh giá")
    private Long sectionAssessmentCode; // Lưu mã như: GK, CK, BT1...
    @NotNull(message = "Điểm số không được để trống")
    @Min(value = 0, message = "Điểm số không được nhỏ hơn 0")
    @Max(value = 10, message = "Điểm số không được lớn hơn 10")
    private Double score;
}