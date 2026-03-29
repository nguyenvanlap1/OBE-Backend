package com.OBE.workflow.permission.account.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountChangePasswordForAdmin {
    @NotBlank(message = "Thiếu mã người dùng không được để trống")
    String username;
    @NotBlank(message = "Thiếu mật khẩu")
    String password;
}
