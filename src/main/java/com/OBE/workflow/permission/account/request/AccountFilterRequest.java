package com.OBE.workflow.permission.account.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountFilterRequest {
    String username;
    Boolean enabled;
    Boolean isSystemAccount;
    String fullName;
}
