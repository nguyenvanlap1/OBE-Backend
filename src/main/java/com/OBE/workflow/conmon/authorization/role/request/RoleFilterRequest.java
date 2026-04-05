package com.OBE.workflow.conmon.authorization.role.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleFilterRequest {
    private String id;   // Tìm theo mã (VD: ROLE_ADMIN)
    private String name; // Tìm theo tên (VD: Quản trị viên)
}