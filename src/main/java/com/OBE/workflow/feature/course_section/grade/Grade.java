package com.OBE.workflow.feature.course_section.grade;

import com.OBE.workflow.feature.course_section.enrollment.Enrollment;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "diem_so",
        indexes = {
                @Index(name = "idx_grade_assessment", columnList = "ma_danh_gia, enrollment_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_grade_enrollment_assessment",
                        columnNames = {"enrollment_id", "ma_danh_gia"}
                )
        }
)
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "ma_danh_gia", nullable = false)
    private String assessmentCode; // Lưu mã như: GK, CK, BT1...

    @Column(name = "diem_so")
    private Double score;
}