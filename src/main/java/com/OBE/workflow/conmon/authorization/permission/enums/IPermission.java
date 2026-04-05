package com.OBE.workflow.conmon.authorization.permission.enums;

import java.util.Set;

public interface IPermission {
    String getId();
    String getName();
    Set<ScopeType> getAllowedScopes();
    Set<IPermission> getChildPermissions(); // Để quản lý logic Create bao hàm Write
    String getDescription();
}