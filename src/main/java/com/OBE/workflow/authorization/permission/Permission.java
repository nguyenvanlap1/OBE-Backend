package com.OBE.workflow.authorization.permission;

import com.OBE.workflow.authorization.permission.enums.ScopeType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phan_quyen")
public class Permission {

    @Id
    @Column(name = "ma_phan_quyen")
    private String id; // Lưu ID như "USER_CREATE", "PROG_WRITE"

    @Column(name = "ten_hien_thi", nullable = false)
    private String name; // Lưu tên tiếng Việt từ Enum

    @Column(name = "mo_ta", length = 1000)
    private String description;

    // Nếu bạn vẫn muốn lưu phạm vi (Scope)
    @ElementCollection(targetClass = ScopeType.class)
    @CollectionTable(name = "phan_quyen_pham_vi", joinColumns = @JoinColumn(name = "ma_phan_quyen"))
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_pham_vi")
    private Set<ScopeType> allowedScopes;
}