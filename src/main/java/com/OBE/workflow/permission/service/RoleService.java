package com.OBE.workflow.permission.service;

import com.OBE.workflow.permission.account.AccountRepository;
import com.OBE.workflow.permission.repository.AccountRoleSubDepartmentRepository;
import com.OBE.workflow.permission.repository.RoleRepository;
import com.OBE.workflow.permission.account.Account;
import com.OBE.workflow.permission.entity.AccountRoleSubDepartment;
import com.OBE.workflow.permission.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final AccountRoleSubDepartmentRepository accountRoleSubDepartmentRepository;

    @Transactional(readOnly = true)
    public List<Role> getRolesByAccount(Account account) {
        List<AccountRoleSubDepartment> accountRoleSubDepartments = accountRoleSubDepartmentRepository.findByAccount(account);
        return accountRoleSubDepartments.stream()
                .map(AccountRoleSubDepartment::getRole)
                .distinct()
                .collect(Collectors.toList());
    }
}
