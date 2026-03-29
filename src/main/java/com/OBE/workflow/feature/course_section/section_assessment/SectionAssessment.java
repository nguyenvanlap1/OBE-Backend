package com.OBE.workflow.feature.course_section.section_assessment;

import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.grade.Grade;
import com.OBE.workflow.feature.course_version.assessment.Assessment;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "cham_trung_chuyen_diem",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_section_assessment_code",
                        columnNames = {"course_section_id", "so_thu_tu_cot_diem"}
                )
        }
)
public class SectionAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="so_thu_tu_cot_diem", nullable = false)
    private Long sectionAssessmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_section_id", nullable = false)
    private CourseSection courseSection;

    // KẾT NỐI TRỰC TIẾP (Nullable = true)
    // Nếu Assessment ở Đề cương bị xóa, ta chỉ cần set field này = null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = true)
    private Assessment originalAssessment;

    // Mối quan hệ với Điểm số: Điểm bây giờ sẽ trỏ vào đây thay vì trỏ trực tiếp vào AssessmentCode
    @Builder.Default
    @OneToMany(mappedBy = "sectionAssessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();
}