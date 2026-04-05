package com.OBE.workflow.conmon.authorization.role.response;

import com.OBE.workflow.conmon.authorization.role.Role;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String id;
    private String name;
    private String description;
    private List<String> permissionIds; // Danh sách ID để dễ handle checkbox ở Frontend

    public static RoleResponse fromEntity(Role entity) {
        if (entity == null) return null;

        return RoleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .permissionIds(entity.getRolePermissions().stream()
                        .map(rp -> rp.getPermission().getId())
                        .collect(Collectors.toList()))
                .build();
    }
}