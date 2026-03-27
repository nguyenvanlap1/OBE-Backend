package com.OBE.workflow.feature.student_class.request;

import com.OBE.workflow.feature.school_year.SchoolYear;
import com.OBE.workflow.feature.student_class.StudentClass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassCreateRequest {
    @NotBlank(message = "Thiếu mã lớp sinh viên")
    String id;
    @NotBlank(message = "Thiếu tên lớp sinh viên")
    String name;

    @NotBlank(message = "Thiếu niên khóa")
    String schoolYearId; // Khóa học (VD: K48)

    @NotBlank(message = "Thiếu mã chương trình đào tạo")
    String educationProgramId;
}