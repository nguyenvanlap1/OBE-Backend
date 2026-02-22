package com.OBE.workflow.feature.permission;

import lombok.Getter;

import java.util.Set;

@Getter
public enum PermissionType {

    // 1. Quyền cơ sở
    VIEW_USER(
            "XEM_NGUOI_DUNG",
            "Xem danh sách người dùng",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(),
            "Xem toàn bộ danh sách người dùng bao gồm xem chi tiết"
    ),

    // 2. Quyền quản lý
    MANAGE_USER(
            "QUAN_LY_NGUOI_DUNG",
            "Quản lý người dùng",
            Set.of(ScopeType.TRUONG),
            Set.of(VIEW_USER),
            "Thêm, sửa, xóa giảng viên và tài khoản người dùng"
    ),

    // 3. Quyền học phần
    VIEW_COURSE(
            "XEM_HOC_PHAN",
            "Xem học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(),
            "Xem danh sách và chi tiết học phần"
    ),

    MANAGE_COURSE(
            "QUAN_LY_HOC_PHAN",
            "Quản lý học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(VIEW_COURSE),
            "Thêm, sửa, xóa học phần"
    ),

    // 4. Quyền chương trình đào tạo
    VIEW_EDUCATION_PROGRAM(
            "XEM_CTDT",
            "Xem chương trình đào tạo",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(),
            "Xem cấu trúc khung chương trình đào tạo"
    ),

    MANAGE_EDUCATION_PROGRAM(
            "QUAN_LY_CTDT",
            "Quản lý chương trình đào tạo",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(VIEW_EDUCATION_PROGRAM),
            "Thiết kế và chỉnh sửa chương trình đào tạo"
    ),

    VIEW_DEPARTMENT(
            "XEM_KHOA",
            "Xem danh sách khoa",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(),
            "Xem danh sách các khoa"
    ),

    MANAGE_DEPARTMENT(
            "QUAN_LY_KHOA",
            "Quản lý khoa",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(VIEW_DEPARTMENT),
            "Thêm, sửa, xóa thông tin khoa"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<PermissionType> childPermissionTypes;
    private final String description;

    PermissionType(String id,
                   String name,
                   Set<ScopeType> allowedScopes,
                   Set<PermissionType> childPermissionTypes,
                   String description) {
        this.id = id;
        this.name = name;
        this.allowedScopes = allowedScopes;
        this.childPermissionTypes = childPermissionTypes;
        this.description = description;
    }

    // Kiểm tra quyền bao hàm
    public boolean hasPermission(PermissionType requiredPermission) {
        if (this == requiredPermission) return true;
        return childPermissionTypes.contains(requiredPermission);
    }
}