package com.OBE.workflow.repository;

import com.OBE.workflow.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    // Đã có sẵn existsById(String id)
}
