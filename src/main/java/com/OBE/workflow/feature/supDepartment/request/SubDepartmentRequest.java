package com.OBE.workflow.feature.supDepartment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubDepartmentRequest {
    @NotBlank(message = "Mã bộ môn không được để trống")
    private String id;

    @NotBlank(message = "Tên bộ môn không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "Mã khoa không được để trống")
    private String departmentId; // Chỉ cần nhận ID của khoa
}