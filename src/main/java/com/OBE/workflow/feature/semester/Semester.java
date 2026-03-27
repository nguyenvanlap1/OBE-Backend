package com.OBE.workflow.feature.semester;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "hoc_ky",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_term_academic_year",
                        columnNames = {"hoc_ky", "nam_hoc"}
                )
        }
)
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "hoc_ky", nullable = false)
    private Integer term;

    @NonNull
    @Column(name = "nam_hoc", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate startDate;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate endDate;

    public String getLabel() {
        return "Học kỳ " + term + " (" + academicYear + ")";
    }
}