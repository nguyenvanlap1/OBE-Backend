package com.OBE.workflow.repository;

import com.OBE.workflow.entity.Account;
import com.OBE.workflow.entity.AccountRole;
import com.OBE.workflow.entity.Id.AccountRoleScopeId;
import com.OBE.workflow.entity.Role;
import com.OBE.workflow.entity.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, AccountRoleScopeId> {

    // Tìm tất cả vai trò và phạm vi của một tài khoản cụ thể
    List<AccountRole> findByAccount(Account account);

    Optional<AccountRole> findByAccountAndRoleAndScope(Account account, Role role, SubDepartment scope);
}
