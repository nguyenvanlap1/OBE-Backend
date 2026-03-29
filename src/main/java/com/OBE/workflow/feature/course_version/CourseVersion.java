package com.OBE.workflow.feature.course_version;

import com.OBE.workflow.feature.course.Course;
import com.OBE.workflow.feature.course_version.assessment.Assessment;
import com.OBE.workflow.feature.course_version.clo.CLO;
import com.OBE.workflow.feature.course_version.co.CO;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "hoc_phan_phien_ban",
        uniqueConstraints = {
                // Ràng buộc: Một tên học phần chỉ thuộc về một mã học phần duy nhất
                // Ngăn chặn việc học phần B lấy tên của học phần A
                @UniqueConstraint(columnNames = {"ma_hoc_phan", "ten_hoc_phan"})
        }
)
@IdClass(CourseVersionId.class) // Định nghĩa cấu trúc khóa hỗn hợp
public class CourseVersion {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_hoc_phan")
    @NonNull
    private Course course;

    @Id
    @Column(name = "so_thu_tu_phien_ban")
    @NonNull
    private Integer versionNumber; // Bạn sẽ tự tăng cái này trong Service (1, 2, 3...)

    @NonNull
    @Column(name = "ten_hoc_phan", nullable = false, unique = false)
    private String name;


    @Column(name = "so_tin_chi", nullable = false)
    @NonNull
    private Integer credits;

    // Sử dụng LocalDate thay cho Integer để quản lý timeline chính xác
    @Column(name = "ap_dung_tu_ngay", nullable = false)
    @NonNull
    private LocalDate fromDate;

    @Column(name = "ap_dung_den_ngay")
    private LocalDate toDate; // Null nghĩa là phiên bản này vẫn đang được áp dụng

    @Builder.Default
    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProgramCourseDetail> programCourseDetails = new ArrayList<>();

    // Trong file CourseVersion.java
    @Builder.Default
    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Assessment> assessments = new ArrayList<>();

    // Thêm vào trong class CourseVersion
    @Builder.Default
    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CLO> clos = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CO> cos = new ArrayList<>();
}