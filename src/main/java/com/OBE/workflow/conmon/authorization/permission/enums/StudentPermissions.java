package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum StudentPermissions implements IPermission {

    STUDENT_CREATE(
            "STUDENT_CREATE", "Thêm mới sinh viên",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép tạo mới hồ sơ sinh viên trên hệ thống"
    ),

    STUDENT_WRITE(
            "STUDENT_WRITE", "Cập nhật thông tin sinh viên",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép chỉnh sửa thông tin cá nhân, lớp học và trạng thái của sinh viên"
    ),

    STUDENT_DELETE(
            "STUDENT_DELETE", "Xóa sinh viên",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép gỡ bỏ hồ sơ sinh viên khỏi hệ thống"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}