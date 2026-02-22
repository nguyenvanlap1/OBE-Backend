package com.OBE.workflow.other_entity_repo.entity.entity;

import com.OBE.workflow.other_entity_repo.entity.entity.Id.AccountRoleSubDepartmentId;
import com.OBE.workflow.feature.supDepartment.SubDepartment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

// 1. Lombok Annotations
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 2. JPA Annotations
@Entity
@Table(name = "tai_khoan_vai_tro")
@IdClass(AccountRoleSubDepartmentId.class) // Sử dụng Id phức hợp cho bảng trung gian
public class AccountRoleSubDepartment {

    // 3. Composite Primary Keys & Relationships
    @Id
    @NonNull
    @ManyToOne
    @JoinColumn(name = "ma_tai_khoan")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Id
    @NonNull
    @ManyToOne
    @JoinColumn(name = "ma_vai_tro")
    private Role role;

    @Id
    @NonNull
    @ManyToOne
    @JoinColumn(name = "ma_bo_mon")
    private SubDepartment subDepartment; // Phạm vi quản lý (theo bộ môn)
}