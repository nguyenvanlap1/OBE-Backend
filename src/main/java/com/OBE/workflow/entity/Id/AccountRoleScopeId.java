package com.OBE.workflow.entity.Id;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleScopeId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String account; // Tên biến phải khớp với tên biến trong LecturerRole
    private String role;     // Tên biến phải khớp với tên biến trong LecturerRole
    private String scope;
}
