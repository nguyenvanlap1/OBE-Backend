package com.OBE.workflow.conmon.authorization.role.response;

import com.OBE.workflow.conmon.authorization.role.Role;
import com.OBE.workflow.conmon.authorization.role.role_permission.RolePermissionResponse;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDetail {
    private String id;
    private String name;
    private String description;
    private List<RolePermissionResponse> rolePermissionResponses;

    public static RoleResponseDetail fromEntity(Role entity) {
        if (entity == null) return null;

        return RoleResponseDetail.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .rolePermissionResponses(entity.getRolePermissions() != null
                        ? entity.getRolePermissions().stream()
                        .map(RolePermissionResponse::fromEntity) // Gọi hàm mapping đã viết
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }
}