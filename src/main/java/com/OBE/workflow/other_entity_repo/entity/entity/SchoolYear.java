package com.OBE.workflow.other_entity_repo.entity.entity;

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
@Table(name = "nien_khoa")
public class SchoolYear {

    // 3. Primary Key
    @Id
    @NonNull
    @Column(name = "khoa") // Giữ "khoa" để khớp với database hiện tại (Batch/Intake)
    private String id;     // VD: "K44", "2022-2026"
}