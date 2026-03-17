package com.OBE.workflow.other_entity_repo.entity;

import com.OBE.workflow.conmon.enums.ScopeType;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @Column(name = "ma_vai_tro")
    private String roleId;

    @NonNull
    @Column(name = "ten_vai_tro", nullable = false)
    private String name;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_bo_mon", nullable = false)
    private SubDepartment subDepartment;

    @Enumerated(EnumType.STRING)
    @Column(name = "pham_vi", nullable = true)
    private ScopeType scopeType;
    // Định nghĩa liệu quyền này có được sài chung
    // và nếu sài chung thì những người dùng nào có quyền assign lớn hơn scopetype thì có quyền truy cập và gán
}