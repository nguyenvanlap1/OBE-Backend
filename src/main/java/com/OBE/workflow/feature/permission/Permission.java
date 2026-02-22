package com.OBE.workflow.feature.permission;

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
@Table(name = "phan_quyen")
public class Permission {

    // 3. Primary Key
    @Id
    @NonNull
    @Column(name = "ma_phan_quyen")
    private String id;

    // 4. Data Fields
    @NonNull
    @Enumerated(EnumType.STRING) // Lưu tên Enum vào DB (VD: "READ", "WRITE") thay vì số 0, 1
    @Column(name = "ten_phan_quyen", nullable = false, length = 1000)
    private PermissionType permissionType;
}