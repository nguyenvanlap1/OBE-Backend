package com.OBE.workflow.feature.course_section.enrollment;

import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.grade.Grade;
import com.OBE.workflow.feature.student.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(
        name = "dang_ky_hoc_phan",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_course_section",
                        columnNames = {"ma_sinh_vien", "ma_lop_hoc_phan"}
                )
        }
)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sinh_vien", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lop_hoc_phan", nullable = false)
    private CourseSection courseSection;

    // Kết nối tới bảng điểm
    @Builder.Default
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();
}
