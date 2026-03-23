package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.lecturer.Lecturer;
import com.OBE.workflow.feature.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lop_hoc_phan")
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_lop_hoc_phan", unique = true, nullable = false)
    private String sectionCode; // VD: CT101-01, CT101-L02

    @Column(name = "hoc_ky")
    private Integer semester;

    @Column(name = "nam_hoc")
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan"),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban")
    })
    private CourseVersion courseVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_giang_vien")
    private Lecturer lecturer;

    // BỔ SUNG: Danh sách sinh viên trong lớp học phần
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dang_ky_hoc_phan", // Đây chính là bảng Enrollment mà mình đã thảo luận
            joinColumns = @JoinColumn(name = "ma_lop_hoc_phan"),
            inverseJoinColumns = @JoinColumn(name = "ma_sinh_vien")
    )
    private Set<Student> students = new HashSet<>();
}