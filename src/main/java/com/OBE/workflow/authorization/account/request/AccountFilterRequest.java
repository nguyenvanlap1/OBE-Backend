package com.OBE.workflow.authorization.account.request;

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
