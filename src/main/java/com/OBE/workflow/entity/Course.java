package com.OBE.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

// 1. Lombok Annotations
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 2. JPA Annotations
@Entity
@Table(name = "hoc_phan")
public class Course {

    // 3. Primary Key
    @Id
    @NonNull
    @Column(name = "ma_hoc_phan")
    private String id; // Ví dụ: CS101, IT001

    // 4. Data Fields
    @NonNull
    @Column(name = "ten_mac_dinh", nullable = false) // Thường tên học phần không nên để trống
    private String defaultName;
}