package com.OBE.workflow.feature.education_program.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationProgramResponse {
    private String id;
    private String name;
    private String educationLevel;
    private Integer requiredCredits;

    // Thông tin bộ môn & khoa
    private String subDepartmentId;
    private String subDepartmentName;
    private String departmentId;
    private String departmentName;

    // Danh sách niên khóa (Ví dụ: ["2022-2026", "2023-2027"])
    private List<String> schoolYearIds;

    // Số lượng học phần trong chương trình để hiển thị nhanh trên bảng
    private Integer totalCourses;
}