package com.OBE.workflow.authorization.role.role_permission;

import com.OBE.workflow.authorization.permission.PermissionRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, PermissionRoleId> {

    // Tìm tất cả các quyền thuộc về một vai trò cụ thể
    List<RolePermission> findByRoleId(String roleId);

    // Xóa tất cả quyền của một vai trò (Dùng khi cập nhật lại danh sách quyền)
    void deleteByRoleId(String roleId);

    // Kiểm tra một vai trò có sở hữu một quyền cụ thể nào đó không
    boolean existsByRoleIdAndPermissionId(String roleId, String permissionId);
}