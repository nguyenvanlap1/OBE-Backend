package com.OBE.workflow.other_entity_repo.entity.repository;

import com.OBE.workflow.other_entity_repo.entity.entity.Account;
import com.OBE.workflow.other_entity_repo.entity.entity.AccountRoleSubDepartment;
import com.OBE.workflow.other_entity_repo.entity.entity.Id.AccountRoleSubDepartmentId;
import com.OBE.workflow.other_entity_repo.entity.entity.Role;
import com.OBE.workflow.feature.supDepartment.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRoleSubDepartmentRepository extends JpaRepository<AccountRoleSubDepartment, AccountRoleSubDepartmentId> {

    // Tìm tất cả vai trò và phạm vi của một tài khoản cụ thể
    List<AccountRoleSubDepartment> findByAccount(Account account);

    Optional<AccountRoleSubDepartment> findByAccountAndRoleAndSubDepartment(Account account, Role role, SubDepartment subDepartment);

    boolean existsBySubDepartment(SubDepartment subDepartment);
}
