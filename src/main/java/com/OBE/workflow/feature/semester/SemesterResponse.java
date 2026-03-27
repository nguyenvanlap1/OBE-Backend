package com.OBE.workflow.feature.semester;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterResponse {
    private Long id;
    private Integer term;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private String label; // Thêm label để Frontend hiển thị dễ hơn
    /**
     * Chuyển đổi từ Entity sang DTO
     */
    public static SemesterResponse fromEntity(Semester semester) {
        if (semester == null) return null;
        return SemesterResponse.builder()
                .id(semester.getId())
                .term(semester.getTerm())
                .academicYear(semester.getAcademicYear())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .label(semester.getLabel()) // Tận dụng helper method từ Entity
                .build();
    }
}