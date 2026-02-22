package com.OBE.workflow.feature.supDepartment.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubDepartmentResponse {
    private String id;
    private String name;
    private String description;
    private String departmentId;   // Trả về ID khoa để dễ xử lý logic
    private String departmentName; // Trả về tên khoa để hiển thị lên bảng (UI)
}