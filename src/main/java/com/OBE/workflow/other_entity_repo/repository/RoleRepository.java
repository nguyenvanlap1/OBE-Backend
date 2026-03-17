package com.OBE.workflow.other_entity_repo.repository;

import com.OBE.workflow.other_entity_repo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
