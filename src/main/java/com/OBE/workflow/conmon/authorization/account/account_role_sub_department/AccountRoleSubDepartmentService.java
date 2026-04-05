package com.OBE.workflow.conmon.authorization.account.account_role_sub_department;

import com.OBE.workflow.conmon.authorization.account.Account;
import com.OBE.workflow.conmon.authorization.account.AccountRepository;
import com.OBE.workflow.conmon.authorization.role.Role;
import com.OBE.workflow.conmon.authorization.role.RoleRepository;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountRoleSubDepartmentService {

    private final AccountRoleSubDepartmentRepository repository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final SubDepartmentRepository subDepartmentRepository;

    /**
     * THÊM: Gán vai trò và bộ môn cho tài khoản
     */
    public void create(AccountRoleSubDepartmentRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Account not found"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Role not found"));
        SubDepartment subDept = subDepartmentRepository.findById(request.getSubDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "SubDepartment not found"));

        AccountRoleSubDepartment entity = AccountRoleSubDepartment.builder()
                .account(account)
                .role(role)
                .subDepartment(subDept)
                .build();

        repository.save(entity);
    }

    /**
     * XÓA: Gỡ bỏ một quyền cụ thể
     */
    public void delete(String accountId, String roleId, String subDeptId) {
        AccountRoleSubDepartmentId id = new AccountRoleSubDepartmentId(accountId, roleId, subDeptId);
        if (!repository.existsById(id)) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Record not found to delete");
        }
        repository.deleteById(id);
    }
}