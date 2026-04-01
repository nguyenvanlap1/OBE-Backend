package com.OBE.workflow.authorization.role;
import com.OBE.workflow.authorization.role.role_permission.RolePermission;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private String id;

    @NonNull
    @Column(name = "ten_vai_tro", nullable = false)
    private String name;

    @Column(name="mieu_ta")
    private String description;
    
    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions = new ArrayList<>();
}