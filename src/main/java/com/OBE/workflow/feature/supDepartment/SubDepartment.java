package com.OBE.workflow.feature.supDepartment;

import com.OBE.workflow.feature.department.Department;
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
@Table(name = "khoa_bo_mon")
public class SubDepartment {

    // 3. Primary Key
    @Id
    @NonNull
    @Column(name = "ma_bo_mon")
    private String id;

    // 4. Data Fields
    @NonNull
    @Column(name = "ten_bo_mon", nullable = false)
    private String name;

    @Column(name = "mieu_ta_khac")
    private String description;

    // 5. Relationships
    @NonNull
    @ManyToOne
    @JoinColumn(name = "ma_khoa", nullable = false) // Khóa ngoại trỏ đến thực thể Department
    private Department department;
}