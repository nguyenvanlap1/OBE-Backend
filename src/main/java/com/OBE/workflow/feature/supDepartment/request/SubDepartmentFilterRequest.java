package com.OBE.workflow.feature.supDepartment.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubDepartmentFilterRequest {
    private String id;
    private String name;
    private String departmentId; // Thêm trường này để lọc bộ môn theo khoa
}