package com.OBE.workflow.conmon.authorization.role.role_permission;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {
    private String roleId;
    private String permissionId;
    private String permissionName;
    private String scopeType;
    /**
     * Chuyển đổi từ Entity sang Response DTO
     * @param entity Thực thể RolePermission từ Database
     * @return Đối tượng RolePermissionResponse đã được làm phẳng
     */
    public static RolePermissionResponse fromEntity(RolePermission entity) {
        if (entity == null) return null;
        return RolePermissionResponse.builder()
                .roleId(entity.getRole().getId())
                .permissionId(entity.getPermission().getId())
                .permissionName(entity.getPermission().getName())
                .scopeType(entity.getScopeType().name())
                .build();
    }
}