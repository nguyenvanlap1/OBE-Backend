package com.OBE.workflow.feature.department.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DepartmentRequest {
    @NotBlank(message = "Mã khoa hoặc trường là bắt buộc")
    private String id;
    @NotBlank(message = "Tên khoa hoặc trường là bắt buộc")
    private String name;
    private String description;
}
