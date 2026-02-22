package com.OBE.workflow.education_program; // Đổi package cho đúng Feature

import com.OBE.workflow.feature.supDepartment.SubDepartment;
import com.OBE.workflow.other_entity_repo.entity.entity.CourseVersion;
import com.OBE.workflow.other_entity_repo.entity.entity.SchoolYear;
import jakarta.persistence.*;
import lombok.*;

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
    private String name;

    @Column(name = "trinh_do_dao_tao") // Ví dụ: Đại học, Cao đẳng
    private String educationLevel;

    @Column(name = "so_tin_chi_yeu_cau") // Cần để tính toán tiến độ tích lũy
    private Integer requiredCredits;

    // --- Relationships ---

    @ManyToOne
    @JoinColumn(name = "ma_bo_mon")
    private SubDepartment subDepartment;

    @ManyToMany
    @JoinTable(
            name = "chuong_trinh_dao_tao_nien_khoa",
            joinColumns = @JoinColumn(name = "ma_chuong_trinh_dao_tao"),
            inverseJoinColumns = @JoinColumn(name = "khoa")
    )
    private List<SchoolYear> schoolYears;

    @ManyToMany
    @JoinTable(
            name = "chi_tiet_chuong_trinh_dao_tao",
            joinColumns = @JoinColumn(name = "ma_chuong_trinh_dao_tao"),
            inverseJoinColumns = @JoinColumn(name = "id_hoc_phan_phien_ban")
    )
    private List<CourseVersion> courseVersions; // Quan trọng nhất để lấy CLOs/PLOs
}