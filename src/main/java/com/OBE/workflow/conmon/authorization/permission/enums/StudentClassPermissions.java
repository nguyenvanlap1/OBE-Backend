package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum StudentClassPermissions implements IPermission {

    STUDENT_CLASS_CREATE(
            "STUDENT_CLASS_CREATE", "Thêm mới lớp sinh viên",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép tạo mới các lớp hành chính (VD: DI2296A1)"
    ),

    STUDENT_CLASS_WRITE(
            "STUDENT_CLASS_WRITE", "Cập nhật lớp sinh viên",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép chỉnh sửa tên lớp, niên khóa hoặc chương trình đào tạo của lớp"
    ),

    STUDENT_CLASS_DELETE(
            "STUDENT_CLASS_DELETE", "Xóa lớp sinh viên",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép gỡ bỏ lớp sinh viên khỏi hệ thống (chỉ khi lớp trống)"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
