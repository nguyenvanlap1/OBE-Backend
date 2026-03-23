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

    // thuộc bộ môn nào
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_bo_mon", nullable = false)
    private SubDepartment subDepartment;
}