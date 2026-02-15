package com.OBE.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tai_khoan")
// @Builder <--- XÓA DÒNG NÀY ĐI
public class Account {

    @Id
    @NonNull
    @Column(name = "ten_dang_nhap", length = 50)
    private String username;

    @NonNull
    @Column(name = "mat_khau", nullable = false)
    private String password;

    @Column(name = "trang_thai", nullable = false)
    private boolean enabled = true;

    @Column(name = "la_quan_tri_vien_he_thong", nullable = false)
    private boolean isSystemAccount = false;

    @Builder // <--- Chỉ đặt ở đây để Builder chỉ nhận 2 tham số này
    public Account(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }
}