package com.OBE.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

// 1. Lombok Annotations
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 2. JPA Annotations
@Entity
@Table(name = "chuong_trinh_dao_tao")
public class EducationProgram {

    // 3. Primary Key
    @Id
    @NonNull
    @Column(name = "ma_chuong_trinh_dao_tao")
    private String id;

    // 4. Data Fields
    @NonNull
    @Column(name = "ten_chuong_trinh_dao_tao", nullable = false)
    private String name;

    @Column(name = "ten_goi_bang")
    private String degreeName;

    @Column(name = "trinh_do_dao_tao")
    private String educationLevel;

    @Column(name = "so_tin_chi_yeu_cau")
    private Integer requiredCredits;

    @Column(name = "hinh_thuc_dao_tao")
    private String trainingForm;

    @Column(name = "thoi_gian_dao_tao")
    private String trainingTime;

    @Column(name = "doi_tuong_tuyen_sinh")
    private String admissionTarget;

    @Column(name = "dieu_kien_tot_nghiep")
    private String graduationCondition;

    // 5. Relationships
    @ManyToOne
    @JoinColumn(name = "ma_bo_mon")
    private SubDepartment subDepartment;

    @ManyToMany
    @JoinTable(
            name = "chuong_trinh_dao_tao_nien_khoa",
            joinColumns = @JoinColumn(name = "ma_chuong_trinh_dao_tao"),
            inverseJoinColumns = @JoinColumn(name = "khoa"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"ma_chuong_trinh_dao_tao", "khoa"})
    )
    private List<SchoolYear> schoolYears;

    @ManyToMany
    @JoinTable(
            name = "chi_tiet_chuong_trinh_dao_tao",
            joinColumns = @JoinColumn(name = "ma_chuong_trinh_dao_tao"),
            inverseJoinColumns = @JoinColumn(name = "id_hoc_phan_phien_ban"), // Đổi 'id' thành tên rõ ràng hơn
            uniqueConstraints = @UniqueConstraint(columnNames = {"ma_chuong_trinh_dao_tao", "id_hoc_phan_phien_ban"})
    )
    private List<CourseVersion> courseVersions;
}