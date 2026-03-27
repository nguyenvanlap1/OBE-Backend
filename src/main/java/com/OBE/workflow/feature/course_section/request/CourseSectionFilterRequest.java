package com.OBE.workflow.feature.course_section.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSectionFilterRequest {
    private String id;
    private Integer semesterTerm;
    private String semesterAcademicYear;

    // Thông tin học phần liên quan
    private String courseId;
    private String courseVersionName;
    private Integer versionNumber;

    // Thông tin giảng viên
    private String lecturerId;
    private String lecturerName;

    private String subDepartmentId;
    private String subDepartmentName;
}
