package com.OBE.workflow.permission.repository;

import com.OBE.workflow.permission.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
