package com.OBE.workflow.service;

import com.OBE.workflow.repository.AccountRepository;
import com.OBE.workflow.repository.AccountRoleRepository;
import com.OBE.workflow.repository.RoleRepository;
import com.OBE.workflow.entity.Account;
import com.OBE.workflow.entity.AccountRole;
import com.OBE.workflow.entity.Role;
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
    private final AccountRoleRepository accountRoleRepository;

    @Transactional(readOnly = true)
    public List<Role> getRolesByAccount(Account account) {
        List<AccountRole> accountRoles = accountRoleRepository.findByAccount(account);
        return accountRoles.stream()
                .map(AccountRole::getRole)
                .distinct()
                .collect(Collectors.toList());
    }
}
