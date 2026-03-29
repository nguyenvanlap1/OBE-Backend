package com.OBE.workflow.feature.course_section.grade;

import com.OBE.workflow.feature.course_section.enrollment.Enrollment;
import com.OBE.workflow.feature.course_section.section_assessment.SectionAssessment; // Import chạm trung chuyển
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
                // Chỉ mục index theo trạm trung chuyển để truy vấn bảng điểm nhanh hơn
                @Index(name = "idx_grade_section_assessment", columnList = "section_assessment_id, enrollment_id")
        },
        uniqueConstraints = {
                // Đảm bảo mỗi sinh viên trong một lớp chỉ có duy nhất một đầu điểm cho mỗi bài đánh giá
                @UniqueConstraint(
                        name = "uq_grade_enrollment_section_assessment",
                        columnNames = {"enrollment_id", "section_assessment_id"}
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

    // KẾT NỐI VỚI CHẠM TRUNG CHUYỂN TẠI ĐÂY:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_assessment_id", nullable = false)
    private SectionAssessment sectionAssessment;

    @Column(name = "diem_so")
    private Double score;
}