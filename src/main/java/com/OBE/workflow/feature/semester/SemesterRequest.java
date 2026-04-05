package com.OBE.workflow.feature.semester;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
public class SemesterRequest {
    @NotNull(message = "Thiếu trường học kì")
    private Integer term;
    @NotNull(message = "Thiếu năm học")
    private String academicYear;
    @NotNull(message = "Thiếu ngày bắt đầu học kì")
    private LocalDate startDate;
    @NotNull(message = "Thiếu ngày kết thúc học kì")
    private LocalDate endDate;

    // Hàm tự kiểm tra logic
    @AssertTrue(message = "Ngày bắt đầu phải trước ngày kết thúc")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Để @NotNull xử lý nếu bị null
        }
        return startDate.isBefore(endDate);
    }
}