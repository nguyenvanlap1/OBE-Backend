package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum AcademicPermissions implements IPermission {
    PROG_WRITE(
            "PROG_WRITE", "Sửa CTĐT",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA, ScopeType.BO_MON), Set.of(), "Sửa khung chương trình"
    ),
    PROG_CREATE(
            "PROG_CREATE", "Tạo CTĐT",
            Set.of(ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(PROG_WRITE),
            "Tạo chương trình mới"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}