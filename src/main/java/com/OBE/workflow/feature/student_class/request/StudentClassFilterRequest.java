package com.OBE.workflow.feature.student_class.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassFilterRequest {
    String id;
    String name;

    String schoolYearId; // Khóa học (VD: K48)

    String educationProgramId;
    String educationProgramName;

    String subDepartmentId;
    String subDepartmentName;

    String departmentId;
    String departmentName;
}
