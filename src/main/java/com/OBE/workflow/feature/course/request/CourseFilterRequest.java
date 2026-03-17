package com.OBE.workflow.feature.course.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CourseFilterRequest {

    // ===== Filter theo thông tin Course =====
    private String id;
    private String defaultName;
    private String subDepartmentId;

    // Thêm Filter theo Khoa (Department)
    private String departmentId;

    // Thêm Filter theo Chương trình đào tạo (Education Program)
    private String educationProgramId;

    // ===== Filter theo thông tin version hiện hành =====
    private Integer versionNumber;
    private String name;       // tên học phần ở version
    private Integer credits;

    // Filter theo khoảng thời gian áp dụng
    private LocalDate fromDate;
    private LocalDate toDate;
}