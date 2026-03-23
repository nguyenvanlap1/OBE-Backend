package com.OBE.workflow.feature.course_version.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseVersionFilterRequest {
    private String courseId;
    private Integer versionNumber;
    private Integer credits;

    // Đặt là courseName cho rõ nghĩa,
    // sau đó map vào Specification.hasCourseName(filter.getCourseName())
    private String courseName;

    private Boolean active;
    private String subDepartmentId;
    private String departmentId;
    private String educationProgramId;
}