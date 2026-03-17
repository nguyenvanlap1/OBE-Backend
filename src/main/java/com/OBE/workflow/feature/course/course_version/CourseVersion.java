package com.OBE.workflow.feature.course.course_version;

import com.OBE.workflow.feature.course.Course;
import com.OBE.workflow.feature.course.assessment_component.Assessment;
import com.OBE.workflow.feature.course.clo.CLO;
import com.OBE.workflow.feature.course.co.CO;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoc_phan_phien_ban")
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

    @Column(name = "ten_hoc_phan", nullable = false)
    @NonNull
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

    // Trong Entity CourseVersion
    @ManyToMany(mappedBy = "courseVersions", fetch = FetchType.LAZY)
    @JsonIgnore // Cách nhanh nhất: Chặn Jackson không cho quét vào biến này khi tạo JSON
    private List<EducationProgram> educationPrograms;

    // Trong file CourseVersion.java
    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Assessment> assessments;

    // Thêm vào trong class CourseVersion
    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CLO> clos;

    @OneToMany(
            mappedBy = "courseVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CO> cos;
}