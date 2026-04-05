package com.OBE.workflow.conmon.authorization.permission.enums;

import java.util.List;

public class PermissionRegistry {
    public static List<IPermission[]> getAllPermissions() {
        return List.of(
                AcademicPermissions.values(),
                UserPermissions.values(),
                DepartmentPermissions.values(),
                SubDepartmentPermissions.values(),
                CourseVersionPermissions.values(),
                CourseSectionPermissions.values(),
                LecturerPermissions.values(),
                StudentPermissions.values(),
                StudentClassPermissions.values(),
                SchoolYearPermissions.values(),
                SemesterPermissions.values(),
                EducationProgramPermissions.values()
        );
    }
}