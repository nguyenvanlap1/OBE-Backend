package com.OBE.workflow.conmon.authorization.account.request;

import com.OBE.workflow.conmon.authorization.account.account_role_sub_department.AccountRoleSubDepartmentRequest;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDetail {

    @NotBlank(message = "Username không được để trống")
    String username;

    @NotNull(message = "Trạng thái kích hoạt không được để trống")
    boolean enabled;

    @Valid // Quan trọng: Để Validate từng phần tử bên trong List
    List<AccountRoleSubDepartmentRequest> accountRoleSubDepartmentResponses;

    public void checkDuplicateRoles(List<AccountRoleSubDepartmentRequest> requests) {
        long uniqueCount = requests.stream()
                .map(req -> req.getRoleId() + "-" + req.getSubDepartmentId())
                .distinct()
                .count();

        if (uniqueCount < requests.size()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Phát hiện gán trùng lặp quyền trong cùng một đơn vị!");
        }
    }
}