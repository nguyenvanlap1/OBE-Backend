package com.OBE.workflow.feature.course.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CourseResponse {

    // ===== Thông tin cố định của học phần =====
    private String id;
    private String defaultName;
    private String subDepartmentId;

    // ===== Thông tin phiên bản hiện hành =====
    private Integer versionNumber;
    private String name;       // Tên học phần ở version hiện hành
    private Integer credits;   // Số tín chỉ hiện hành
    private LocalDate fromDate;
    private LocalDate toDate;  // null = đang áp dụng
}