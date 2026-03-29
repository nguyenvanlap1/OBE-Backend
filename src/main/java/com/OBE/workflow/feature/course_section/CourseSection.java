package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.feature.course_section.enrollment.Enrollment;
import com.OBE.workflow.feature.course_section.section_assessment.SectionAssessment;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.lecturer.Lecturer;
import com.OBE.workflow.feature.semester.Semester;
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
@Table(name = "lop_hoc_phan")
public class CourseSection {

    @Id
    @Column(name = "ma_lop_hoc_phan")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan", nullable = false),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban", nullable = false)
    })
    private CourseVersion courseVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_giang_vien", nullable = false)
    private Lecturer lecturer;

    // THAY ĐỔI TẠI ĐÂY:
    // Trỏ đến Enrollment thay vì Student. MappedBy là tên biến 'courseSection' trong class Enrollment.
    @Builder.Default
    @OneToMany(mappedBy = "courseSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "courseSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SectionAssessment> sectionAssessments = new ArrayList<>();
}