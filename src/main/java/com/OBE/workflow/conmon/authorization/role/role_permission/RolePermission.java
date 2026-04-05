package com.OBE.workflow.conmon.authorization.role.role_permission;

import com.OBE.workflow.conmon.authorization.permission.Permission;
import com.OBE.workflow.conmon.authorization.permission.PermissionRoleId;
import com.OBE.workflow.conmon.authorization.role.Role;
import com.OBE.workflow.conmon.authorization.permission.enums.ScopeType;
import jakarta.persistence.*;
import lombok.*;

// 1. Lombok Annotations
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 2. JPA Annotations
@Entity
@Table(name = "vai_tro_phan_quyen")
@IdClass(PermissionRoleId.class) // Quản lý khóa phức hợp (Role + Permission)
public class RolePermission {

    // 3. Composite Primary Keys & Relationships
    @Id
    @NonNull
    @ManyToOne
    @JoinColumn(name = "ma_vai_tro")
    private Role role;

    @Id
    @NonNull
    @ManyToOne
    @JoinColumn(name = "ma_phan_quyen")
    private Permission permission;

    // 4. Data Fields
    @NonNull
    @Enumerated(EnumType.STRING) // Quan trọng để lưu "DEPARTMENT", "SYSTEM" thay vì số
    @Column(name = "pham_vi", nullable = false)
    private ScopeType scopeType;
}