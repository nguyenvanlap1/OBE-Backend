package com.OBE.workflow.feature.lecturer.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LecturerFilterRequest {
    private String id;
    private String fullName;
    private String gender;

    // Thêm trường này để lọc giảng viên theo bộ môn
    private String subDepartmentId;
}