package com.OBE.workflow.authorization.account.account_role_sub_department;
import lombok.*;
// 1. Lombok Annotations
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleSubDepartmentResponse {
    private String accountId;
    private String roleId;
    private String roleName;
    private String subDepartmentId;
    private String subDepartmentName; // Phạm vi quản lý (theo bộ môn)
    private String departmentId;
    private String departmentName;
    /**
     * Chuyển đổi từ Entity sang Response DTO
     * @param entity Đối tượng AccountRoleSubDepartment từ database
     * @return AccountRoleSubDepartmentResponse
     */
    public static AccountRoleSubDepartmentResponse fromEntity(AccountRoleSubDepartment entity) {
        if (entity == null) {
            return null;
        }
        return AccountRoleSubDepartmentResponse.builder()
                .accountId(entity.getAccount().getUsername())
                .roleId(entity.getRole().getId())
                .roleName(entity.getRole().getName())
                .subDepartmentId(entity.getSubDepartment().getId())
                .subDepartmentName(entity.getSubDepartment().getName())
                .departmentId(
                        entity.getSubDepartment().getDepartment().getId()
                )
                .departmentName(
                        entity.getSubDepartment().getDepartment().getName()
                )
                .build();
    }
}
