package com.OBE.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

// 1. Lombok Annotations (Thứ tự ưu tiên logic)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 2. JPA Annotations (Thứ tự ưu tiên mapping)
@Entity
@Table(name = "giang_vien")
public class Lecturer {

    // 3. Primary Key & Relationship (Sử dụng @MapsId)
    @Id
    @Column(name = "ma_gv")
    private String id;

    @NonNull
    @OneToOne
    @MapsId
    @JoinColumn(name = "ma_gv", referencedColumnName = "ten_dang_nhap")
    private Account account;

    // 4. Data Fields
    @NonNull
    @Column(name = "ho_ten", nullable = false)
    private String fullName;

    @NonNull
    @Column(name = "gioi_tinh", nullable = false)
    private String gender;

    // 5. Custom Builder để tối ưu hóa việc tạo đối tượng
    @Builder
    public Lecturer(@NonNull Account account, @NonNull String fullName, @NonNull String gender) {
        this.account = account;
        this.fullName = fullName;
        this.gender = gender;
        // id sẽ được @MapsId tự động lấy từ account.username khi save
    }
}