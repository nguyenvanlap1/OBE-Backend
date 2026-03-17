package com.OBE.workflow.feature.course;

import com.OBE.workflow.feature.sup_department.SubDepartment;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoc_phan")
public class Course {

    @Id
    @NonNull
    @Column(name = "ma_hoc_phan")
    private String id; // VD: CT101

    @NonNull
    @Column(name = "ten_mac_dinh", nullable = false)
    private String defaultName;

    // Một bộ môn quản lý nhiều học phần
    // Một bộ môn quản lý nhiều học phần (Bắt buộc phải có bộ môn)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_bo_mon", nullable = false)
    private SubDepartment subDepartment;
}