package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum SchoolYearPermissions implements IPermission {

    SCHOOL_YEAR_CREATE(
            "SCHOOL_YEAR_CREATE", "Thêm niên khóa",
            Set.of(ScopeType.TRUONG), // Chỉ cấp Trường mới được thêm khóa mới (VD: Khóa 2024)
            Set.of(), "Cho phép tạo mới niên khóa/khóa học trong hệ thống"
    ),

    SCHOOL_YEAR_DELETE(
            "SCHOOL_YEAR_DELETE", "Xóa niên khóa",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép xóa niên khóa (chỉ khi chưa có dữ liệu liên quan)"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
