package com.OBE.workflow.feature.course.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CourseCreateRequest {

    @NotBlank(message = "Mã học phần không được để trống")
    private String id;

    @NotBlank(message = "Tên mặc định không được để trống")
    private String defaultName;

    @NotBlank(message = "Mã bộ môn quản lý là bắt buộc")
    private String subDepartmentId;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ phải ít nhất là 1")
    private Integer credits;

    // Sử dụng LocalDate để lưu ngày bắt đầu áp dụng chính xác
    @NotNull(message = "Ngày bắt đầu áp dụng không được để trống")
    private LocalDate fromDate;

    private LocalDate toDate;
}