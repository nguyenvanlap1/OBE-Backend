package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum UserPermissions implements IPermission {
    USER_DELETE(
            "USER_DELETE", "Xóa người dùng",
            Set.of(ScopeType.TRUONG), Set.of(), "Xóa tài khoản"
    ),
    USER_WRITE(
            "USER_WRITE", "Sửa người dùng",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG), Set.of(), "Sửa hồ sơ"
    ),
    USER_CREATE(
            "USER_CREATE", "Tạo người dùng",
            Set.of(ScopeType.TRUONG),
            Set.of(USER_WRITE), // Tạo quyền bao hàm quyền sửa
            "Thêm tài khoản mới"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}