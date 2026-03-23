package com.OBE.workflow.feature.assessment_score;

import com.OBE.workflow.feature.course_version.assessment.Assessment;
import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.student.Student;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "diem_sinh_vien")
public class AssessmentScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sinh_vien", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lop_hoc_phan", nullable = false)
    private CourseSection courseSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thanh_phan_diem", nullable = false)
    private Assessment assessment;

    @Column(name = "diem_so")
    private Double score; // Ví dụ: 8.5
}