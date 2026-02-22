package com.OBE.workflow.feature.lecturer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class LecturerRequest {
    @NotBlank(message = "Mã giảng viên là bắt buộc")
    private String id;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    // Danh sách mã bộ môn mà giảng viên này trực thuộc
    @NotEmpty(message = "Giảng viên phải thuộc ít nhất một bộ môn")
    private Set<String> subDepartmentIds;
}