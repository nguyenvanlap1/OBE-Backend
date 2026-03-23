package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.education_program.plo.PLO;
import com.OBE.workflow.feature.education_program.po.PO;
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


    @ManyToMany
    @JoinTable(
            name = "chi_tiet_chuong_trinh_dao_tao",
            joinColumns = @JoinColumn(name = "ma_chuong_trinh_dao_tao"),
            inverseJoinColumns = {
                    @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan"),
                    @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban")
            },
            uniqueConstraints = @UniqueConstraint(columnNames = {"ma_chuong_trinh_dao_tao", "ma_hoc_phan", "so_thu_tu_phien_ban"})
            // Ràng buộc quan trọng: Tránh trùng lặp phiên bản học phần trong cùng 1 chương trình
    )
    @NonNull
    private List<CourseVersion> courseVersions;

    // 1. Thêm danh sách Mục tiêu đào tạo (PO)
    @Builder.Default
    @OneToMany(mappedBy = "educationProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PO> pos = new ArrayList<>();

    // 2. Thêm danh sách Chuẩn đầu ra (PLO)
    @Builder.Default
    @OneToMany(mappedBy = "educationProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PLO> plos = new ArrayList<>();
}