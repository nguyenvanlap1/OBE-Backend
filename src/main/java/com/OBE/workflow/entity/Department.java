package com.OBE.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter // Tự tạo tất cả Getter
@Setter // Tự tạo tất cả Setter
@NoArgsConstructor // Tự tạo Constructor không tham số (bắt buộc cho JPA)
@AllArgsConstructor // Tự tạo Constructor đầy đủ tham số
@Builder
@Entity
@Table(name = "truong_khoa")
public class Department {

    @Id
    @Column(name = "ma_khoa")
    @NonNull
    private String id;

    @Column(name = "ten_khoa", nullable = false, unique = true)
    @NonNull
    private String name;

    @Column(name = "mieu_ta_khac")
    private String description;
    // Getters và Setters
}
