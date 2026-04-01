package com.OBE.workflow.authorization.permission.enums;

public class PermissionChecker {
    public static boolean hasPermission(IPermission userHas, IPermission required) {
        if (userHas.getId().equals(required.getId())) return true;

        // Kiểm tra trong các quyền con (Recursive check nếu cần)
        return userHas.getChildPermissions().stream()
                .anyMatch(child -> child.getId().equals(required.getId()));
    }
}