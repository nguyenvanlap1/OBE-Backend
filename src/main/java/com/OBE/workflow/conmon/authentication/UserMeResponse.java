package com.OBE.workflow.conmon.authentication;

import com.OBE.workflow.conmon.authorization.account.Account;
import com.OBE.workflow.conmon.authorization.account.account_role_sub_department.AccountRoleSubDepartment;
import com.OBE.workflow.conmon.authorization.role.role_permission.RolePermission;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserMeResponse {
    private String username;
    private String fullName;
    private boolean isSystemAccount;
    private List<UserAssignmentDTO> assignments;

    public static UserMeResponse fromEntity(Account account) {
        if (account == null) return null;

        return UserMeResponse.builder()
                .username(account.getUsername())
                .fullName(account.getPerson() != null ? account.getPerson().getFullName() : "N/A")
                .isSystemAccount(account.isSystemAccount())
                .assignments(account.getAccountRoleSubDepartments() != null
                        ? account.getAccountRoleSubDepartments().stream()
                        .map(UserAssignmentDTO::fromEntity)
                        .collect(Collectors.toList())
                        : List.of())
                .build();
    }

    @Data
    @Builder
    public static class UserAssignmentDTO {
        private String departmentId;
        private String departmentName;
        private String subDepartmentId;
        private String subDepartmentName;
        private String roleId;
        private String roleName;
        private List<PermissionResponse> permissions;
        // 2. Mapping cho từng lượt gán (Account - Role - SubDepartment)
        public static UserAssignmentDTO fromEntity(AccountRoleSubDepartment mapping) {
            var subDept = mapping.getSubDepartment();
            var dept = subDept.getDepartment();
            var role = mapping.getRole();

            return UserAssignmentDTO.builder()
                    .subDepartmentId(subDept.getId())
                    .subDepartmentName(subDept.getName())
                    .departmentId(dept.getId())
                    .departmentName(dept.getName())
                    .roleId(role.getId())
                    .roleName(role.getName())
                    .permissions(role.getRolePermissions() != null
                            ? role.getRolePermissions().stream()
                            .map(PermissionResponse::fromEntity)
                            .collect(Collectors.toList())
                            : List.of())
                    .build();
        }
    }

    @Data
    @Builder
    public static class PermissionResponse {
        private String id;     // Ví dụ: "USER_CREATE"
        private String name;   // Ví dụ: "Tạo người dùng"
        private String scope;
        private String description;

        // 3. Mapping cho thông tin quyền hạn
        public static PermissionResponse fromEntity(RolePermission rolePermission) {
            if (rolePermission == null) return null;
            return PermissionResponse.builder()
                    .id(rolePermission.getPermission().getId())
                    .name(rolePermission.getPermission().getName())
                    .description(rolePermission.getPermission().getDescription())
                    .scope(rolePermission.getScopeType().name())
                    .build();
        }
    }
}