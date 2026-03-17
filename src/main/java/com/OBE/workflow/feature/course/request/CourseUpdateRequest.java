package com.OBE.workflow.feature.course.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CourseUpdateRequest {

    @NotBlank(message = "Mã học phần không được để trống")
    private String id;

    @NotBlank(message = "Tên mặc định không được để trống")
    private String defaultName;

    @NotBlank(message = "Mã bộ môn quản lý là bắt buộc")
    private String subDepartmentId;

    // CourseVersion
    @NotNull(message = "Sô thứ tự phiên bản cửa học phần phiên bản là bắt buộc")
    private Integer versionNumber; // Bạn sẽ tự tăng cái này trong Service (1, 2, 3...)

    @NotBlank(message = "Tên học phần phiên bản không được để trống")
    private String name;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ phải ít nhất là 1")
    private Integer credits;

    // Sử dụng LocalDate để lưu ngày bắt đầu áp dụng chính xác
    @NotNull(message = "Ngày bắt đầu áp dụng không được để trống")
    private LocalDate fromDate;

    private LocalDate toDate;

    @NotNull(message = "Phải xác định đây là cập nhật hay tạo phiên bản mới")
    private Boolean isNewVersion; // Sử dụng Boolean (Object) thay vì boolean (primitive) để check @NotNull
}