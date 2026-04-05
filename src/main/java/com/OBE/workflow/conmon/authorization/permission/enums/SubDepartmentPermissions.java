package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum SubDepartmentPermissions implements IPermission {

    SUBDEPT_CREATE(
            "SUBDEPT_CREATE",
            "Tạo mới bộ môn",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA), // Khoa cũng có thể tạo bộ môn con của mình
            Set.of(),
            "Thêm một đơn vị cấp bộ môn mới vào khoa chủ quản"
    ),

    SUBDEPT_WRITE(
            "SUBDEPT_WRITE",
            "Chỉnh sửa bộ môn",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA, ScopeType.BO_MON),
            Set.of(),
            "Cập nhật thông tin mô tả, tên bộ môn"
    ),

    SUBDEPT_DELETE(
            "SUBDEPT_DELETE",
            "Xóa bộ môn",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA),
            Set.of(),
            "Gỡ bỏ hoàn toàn một bộ môn khỏi hệ thống"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
