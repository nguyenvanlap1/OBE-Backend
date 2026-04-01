package com.OBE.workflow.authorization.permission.enums;

import java.util.List;

public class PermissionRegistry {
    public static List<IPermission[]> getAllPermissions() {
        return List.of(
                AcademicPermissions.values(),
                UserPermissions.values()
        );
    }
}