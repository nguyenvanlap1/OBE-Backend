package com.OBE.workflow.feature.student_class.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassUpdateRequest {
    @NotBlank(message = "Thiếu mã lớp sinh viên")
    String id;
    @NotBlank(message = "Thiếu mã tên sinh viên")
    String name;
    @NotBlank(message = "Thiếu niên khóa")
    String schoolYearId; // Khóa học (VD: K48)
    @NotBlank(message = "Thiếu mã chương trình đào tạo")
    String educationProgramId;
}
