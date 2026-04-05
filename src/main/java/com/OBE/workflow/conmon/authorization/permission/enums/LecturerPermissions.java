package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum LecturerPermissions implements IPermission {

    LECTURER_CREATE(
            "LECTURER_CREATE", "Thêm mới giảng viên",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép tạo mới hồ sơ giảng viên trên hệ thống"
    ),

    LECTURER_WRITE(
            "LECTURER_WRITE", "Cập nhật thông tin giảng viên",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép chỉnh sửa thông tin cá nhân, trình độ của giảng viên"
    ),

    LECTURER_DELETE(
            "LECTURER_DELETE", "Xóa giảng viên",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép gỡ bỏ hồ sơ giảng viên khỏi hệ thống"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}