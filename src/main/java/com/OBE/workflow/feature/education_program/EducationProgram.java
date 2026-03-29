package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.education_program.plo.PLO;
import com.OBE.workflow.feature.education_program.po.PO;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetail;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.school_year.SchoolYear;
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
@Table(name = "chuong_trinh_dao_tao")
public class EducationProgram {

    @Id
    @Column(name = "ma_chuong_trinh_dao_tao")
    private String id;

    @Column(name = "ten_chuong_trinh_dao_tao", nullable = false)
    @NonNull
    private String name;

    @Column(name = "trinh_do_dao_tao", nullable = false) // Bắt buộc để phân loại
    @NonNull
    private String educationLevel;

    @Column(name = "so_tin_chi_yeu_cau", nullable = false)
    @NonNull
    private Integer requiredCredits;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bo_mon", nullable = false) // Một CTĐT phải thuộc một bộ môn quản lý
    @NonNull
    private SubDepartment subDepartment;

    @ManyToMany
    @JoinTable(
            name = "chuong_trinh_dao_tao_nien_khoa",
            joinColumns = @JoinColumn(name = "ma_chuong_trinh_dao_tao"),
            inverseJoinColumns = @JoinColumn(name = "khoa"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"ma_chuong_trinh_dao_tao", "khoa"})
            // Đảm bảo một niên khóa không bị gán trùng 2 lần vào 1 CTĐT
    )
    private List<SchoolYear> schoolYears;


    // ĐÃ ĐỔI TẠI ĐÂY: Chuyển sang OneToMany để quản lý chi tiết kèm Khối kiến thức
    @Builder.Default
    @OneToMany(mappedBy = "educationProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramCourseDetail> courseDetails = new ArrayList<>();

    // 1. Thêm danh sách Mục tiêu đào tạo (PO)
    @Builder.Default
    @OneToMany(mappedBy = "educationProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PO> pos = new ArrayList<>();

    // 2. Thêm danh sách Chuẩn đầu ra (PLO)
    @Builder.Default
    @OneToMany(mappedBy = "educationProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PLO> plos = new ArrayList<>();
}