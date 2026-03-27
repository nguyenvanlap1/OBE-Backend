package com.OBE.workflow.feature.course_section.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSectionCreateRequest {

    @NotBlank(message = "Mã lớp học phần không được để trống")
    private String id; // VD: CT101-01

    @NotNull(message = "Học kỳ không được để trống")
    private Integer semesterTerm; // 1, 2, 3
    @NotBlank(message = "Năm học không được để trống")
    private String semesterAcademicYear; // VD: 2025-2026

    // Thông tin để link tới CourseVersion (Composite Key)
    @NotBlank(message = "Mã học phần không được để trống")
    private String courseId;
    @NotNull(message = "Mã phiên bản học phần không được để trống")
    private Integer versionNumber; // Bạn sẽ tự tăng cái này trong Service (1, 2, 3...)

    // Giảng viên phụ trách
    @NotBlank(message = "Mã giảng viên không được để trống")
    private String lecturerId;
}
