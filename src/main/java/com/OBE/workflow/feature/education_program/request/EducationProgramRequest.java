package com.OBE.workflow.feature.education_program.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationProgramRequest {

    @NotBlank(message = "Mã chương trình đào tạo không được để trống")
    private String id;

    @NotBlank(message = "Tên chương trình đào tạo không được để trống")
    private String name;

    @NotBlank(message = "Trình độ đào tạo không được để trống")
    private String educationLevel;

    @NotNull(message = "Số tín chỉ yêu cầu không được để trống")
    @Min(value = 1, message = "Số tín chỉ yêu cầu phải ít nhất là 1")
    private Integer requiredCredits;

    @NotBlank(message = "Mã bộ môn quản lý là bắt buộc")
    private String subDepartmentId;

    @NotEmpty(message = "Chương trình đào tạo phải thuộc ít nhất một niên khóa")
    private List<String> schoolYearIds;

    // Đối với CourseVersion, vì khóa ngoại là khóa phức hợp (composite key)
    // Ta cần một DTO nhỏ để hứng cặp (ma_hoc_phan, so_thu_tu_phien_ban)
}