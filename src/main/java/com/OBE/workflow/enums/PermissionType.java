package com.OBE.workflow.enums;

import lombok.Getter;
import java.util.Set;
import java.util.EnumSet;

@Getter
public enum PermissionType {
    // 1. Quản lý người dùng
    MANAGE_USER(
            "quan_ly_nguoi_dung",
            "Quản lý người dùng",
            EnumSet.of(ScopeType.TRUONG),
            "Thêm, sửa, xóa giảng viên và tài khoản người dùng"
    ),

    // 2. Quyền liên quan đến Khoa/Trường
    VIEW_DEPARTMENT(
            "xem_danh_sach_khoa",
            "Xem danh sách khoa",
            EnumSet.of(ScopeType.TRUONG),
            "Xem danh sách các khoa hoặc trường trong hệ thống"
    ),

    MANAGE_DEPARTMENT(
            "quan_ly_khoa",
            "Quản lý khoa",
            EnumSet.of(ScopeType.TRUONG),
            "Thêm, sửa, xóa thông tin khoa hoặc trường"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final String description;

    PermissionType(String id, String name, Set<ScopeType> allowedScopes, String description) {
        this.id = id;
        this.name = name;
        this.allowedScopes = allowedScopes;
        this.description = description;
    }

    public boolean supportsScope(ScopeType scope) {
        return allowedScopes.contains(scope);
    }
}