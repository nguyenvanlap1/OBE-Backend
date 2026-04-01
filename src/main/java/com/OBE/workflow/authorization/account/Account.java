package com.OBE.workflow.authorization.account;

import com.OBE.workflow.authorization.account.account_role_sub_department.AccountRoleSubDepartment;
import com.OBE.workflow.authorization.account.person.Person;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tai_khoan")
public class Account {

    @Id
    @Column(name = "ten_dang_nhap", length = 50)
    private String username; // Sẽ khớp với mã của Person

    @NonNull
    @Column(name = "mat_khau", nullable = false)
    private String password;

    @Column(name = "trang_thai", nullable = false)
    private boolean enabled = true;

    @Column(name = "la_quan_tri_vien_he_thong", nullable = false)
    private boolean isSystemAccount = false;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Khóa chính của Account sẽ được map từ ID của Person
    @JoinColumn(name = "ten_dang_nhap")
    private Person person;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AccountRoleSubDepartment> accountRoleSubDepartments;

    @Builder
    public Account(@NonNull String password, @NonNull Person person) {
        this.password = password;
        this.person = person;
        // this.username sẽ tự động lấy từ person.getId() khi save
    }
}