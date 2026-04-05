package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum CourseVersionPermissions implements IPermission {

    COURSE_VERSION_CREATE(
            "COURSE_VERSION_CREATE", "Tạo mới phiên bản học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép tạo học phần mới (V1) hoặc tạo phiên bản kế tiếp (V+1)"
    ),

    COURSE_VERSION_WRITE(
            "COURSE_VERSION_WRITE", "Cập nhật phiên bản học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép chỉnh sửa CO, CLO, Assessment và Mapping của phiên bản"
    ),

    COURSE_VERSION_DELETE(
            "COURSE_VERSION_DELETE", "Xóa phiên bản học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Gỡ bỏ một phiên bản học phần cụ thể khỏi hệ thống"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
