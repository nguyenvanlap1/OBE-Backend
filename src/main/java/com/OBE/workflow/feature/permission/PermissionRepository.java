package com.OBE.workflow.feature.permission;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    // Đã có sẵn existsById(String id)
}
