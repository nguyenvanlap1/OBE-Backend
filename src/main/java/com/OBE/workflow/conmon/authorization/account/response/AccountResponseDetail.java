package com.OBE.workflow.conmon.authorization.account.response;

import com.OBE.workflow.conmon.authorization.account.Account; // Giả sử đây là path của Entity
import com.OBE.workflow.conmon.authorization.account.account_role_sub_department.AccountRoleSubDepartmentResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDetail {
    String id;
    String username;
    boolean enabled;
    boolean isSystemAccount;
    String fullName;
    List<AccountRoleSubDepartmentResponse> accountRoleSubDepartmentResponses;
    /**
     * Chuyển đổi từ Entity sang DTO (Response)
     */
    public static AccountResponseDetail fromEntity(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponseDetail.builder()
                .id(account.getUsername())
                .username(account.getUsername())
                .enabled(account.isEnabled())
                .isSystemAccount(account.isSystemAccount())
                .fullName(account.getPerson().getFullName())
                .accountRoleSubDepartmentResponses(account.getAccountRoleSubDepartments().stream().map(
                        AccountRoleSubDepartmentResponse::fromEntity
                ).toList())
                .build();
    }
}