package com.OBE.workflow.feature.officer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OfficerRequest {
    @NotBlank(message = "Mã giảng viên là bắt buộc")
    private String id;
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    @NotBlank(message = "Giới tính không được để trống")
    private String gender;
}
