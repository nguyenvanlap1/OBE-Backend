package com.OBE.workflow.feature.student_class.response;

import com.OBE.workflow.feature.department.Department;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.school_year.SchoolYear;
import com.OBE.workflow.feature.student_class.StudentClass;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentClassResponse {
    String id;
    String name;

    String schoolYearId; // Khóa học (VD: K48)

    String educationProgramId;
    String educationProgramName;

    String subDepartmentId;
    String subDepartmentName;

    String departmentId;
    String departmentName;

    public static StudentClassResponse fromEntity(StudentClass entity) {
        if (entity == null) return null;

        SchoolYear schoolYear = entity.getSchoolYear();
        EducationProgram educationProgram = entity.getEducationProgram();

        // Truy xuất an toàn theo phân cấp: CTĐT -> Bộ môn -> Khoa
        SubDepartment subDepartment = (educationProgram != null) ? educationProgram.getSubDepartment() : null;
        Department department = (subDepartment != null) ? subDepartment.getDepartment() : null;

        return StudentClassResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .schoolYearId(schoolYear != null ? schoolYear.getId() : null)
                .educationProgramId(educationProgram != null ? educationProgram.getId() : null)
                .educationProgramName(educationProgram != null ? educationProgram.getName() : null)
                .subDepartmentId(subDepartment != null ? subDepartment.getId() : null)
                .subDepartmentName(subDepartment != null ? subDepartment.getName() : null)
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getName() : null)
                .build();
    }
}