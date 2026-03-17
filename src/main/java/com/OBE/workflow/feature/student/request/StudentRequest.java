package com.OBE.workflow.feature.student.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StudentRequest {
    @NotBlank(message = "Mã sinh viên là bắt buộc")
    private String id;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    // Danh sách mã chương trình đào tạo mà sinh viên theo học (Hỗ trợ song bằng)
    @NotEmpty(message = "Sinh viên phải thuộc ít nhất một chương trình đào tạo")
    private Set<String> educationProgramIds;
}