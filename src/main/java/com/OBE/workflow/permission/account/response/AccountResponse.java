package com.OBE.workflow.permission.account.response;

import com.OBE.workflow.permission.account.Account; // Giả sử đây là path của Entity
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    String id;
    String username;
    boolean enabled;
    boolean isSystemAccount;
    String fullName;

    /**
     * Chuyển đổi từ Entity sang DTO (Response)
     */
    public static AccountResponse fromEntity(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponse.builder()
                .id(account.getUsername())
                .username(account.getUsername())
                .enabled(account.isEnabled())
                .isSystemAccount(account.isSystemAccount())
                .fullName(account.getPerson().getFullName())
                .build();
    }
}