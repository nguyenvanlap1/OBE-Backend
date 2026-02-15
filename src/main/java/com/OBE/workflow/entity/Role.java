package com.OBE.workflow.entity;

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
@Table(name = "vai_tro")
public class Role {

    // 3. Primary Key
    @Id
    @NonNull
    @Column(name = "ma_vai_tro")
    private String roleId; // Ví dụ: ADMIN, GIANG_VIEN, TRUONG_BOMON

    @NonNull
    @Column(name = "ten_vai_tro")
    private String name;
}