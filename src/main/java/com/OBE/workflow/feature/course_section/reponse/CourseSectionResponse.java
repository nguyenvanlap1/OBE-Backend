package com.OBE.workflow.feature.course_section.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionResponse {
    private Long id;
    private String sectionCode;
    private Integer semester;
    private String academicYear;

    // Thông tin học phần liên quan
    private String courseId;
    private String courseName;
    private Integer versionNumber;

    // Thông tin giảng viên
    private String lecturerId;
    private String lecturerName;


}
