package com.OBE.workflow.feature.education_program.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationProgramFilterRequest {

    private String id;               // Mã chương trình đào tạo

    private String name;             // Tên chương trình

    private String educationLevel;   // Trình độ đào tạo (Đại học, Cao đẳng...)

    private String subDepartmentId;  // Lọc theo Bộ môn

    private String departmentId;     // Lọc theo Khoa

    private String schoolYearId;     // Lọc theo Niên khóa (ví dụ: 2022)
}