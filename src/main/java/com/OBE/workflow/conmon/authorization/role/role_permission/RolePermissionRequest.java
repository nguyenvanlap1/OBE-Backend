package com.OBE.workflow.conmon.authorization.role.role_permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RolePermissionRequest {

    @NotBlank(message = "Mã vai trò không được để trống")
    private String roleId;

    @NotBlank(message = "Mã quyền không được để trống")
    private String permissionId;

    @NotNull(message = "Phạm vi quyền hạn (Scope) không được để trống")
    private String scopeType; // Gửi lên dưới dạng String (VD: "DEPARTMENT", "FACULTY")
}