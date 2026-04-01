package com.OBE.workflow.authorization.account.account_role_sub_department;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleSubDepartmentRequest {

    @NotBlank(message = "Mã tài khoản không được để trống")
    private String accountId;

    @NotBlank(message = "Mã vai trò không được để trống")
    private String roleId;

    @NotBlank(message = "Mã bộ môn không được để trống")
    private String subDepartmentId;
}