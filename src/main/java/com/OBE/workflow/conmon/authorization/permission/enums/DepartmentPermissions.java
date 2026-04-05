package com.OBE.workflow.conmon.authorization.permission.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum DepartmentPermissions implements IPermission {

    DEPT_CREATE(
            "DEPT_CREATE", "Tạo mới khoa",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Thêm một đơn vị quản lý mới vào hệ thống"
    ),

    DEPT_WRITE(
            "DEPT_WRITE", "Chỉnh sửa khoa",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cập nhật thông tin mô tả, tên hoặc lãnh đạo khoa"
    ),

    DEPT_DELETE(
            "DEPT_DELETE", "Xóa khoa",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Gỡ bỏ hoàn toàn một khoa khỏi hệ thống"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
