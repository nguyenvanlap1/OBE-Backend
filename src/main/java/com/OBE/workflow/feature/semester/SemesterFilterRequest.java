package com.OBE.workflow.feature.semester;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
public class SemesterFilterRequest {
    private Long id;
    private Integer term;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
}