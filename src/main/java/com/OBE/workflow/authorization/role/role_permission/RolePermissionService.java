package com.OBE.workflow.authorization.role.role_permission;

import com.OBE.workflow.authorization.permission.Permission;
import com.OBE.workflow.authorization.permission.PermissionRepository;
import com.OBE.workflow.authorization.permission.enums.ScopeType;
import com.OBE.workflow.authorization.role.Role;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void saveRolePermissions(Role role, List<RolePermissionRequest> requests) {
        List<RolePermission> rolePermissions = requests.stream().map(req -> {
            Permission permission = permissionRepository.findById(req.getPermissionId())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Quyền không tồn tại: " + req.getPermissionId()));

            return RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .scopeType(ScopeType.valueOf(req.getScopeType()))
                    .build();
        }).collect(Collectors.toList());

        rolePermissionRepository.saveAll(rolePermissions);
    }

    @Transactional
    public void updateRolePermissions(Role role, List<RolePermissionRequest> requests) {
        // 1. Lấy danh sách quyền hiện tại từ Database
        List<RolePermission> currentPermissions = rolePermissionRepository.findByRoleId(role.getId());

        // 2. Chuyển requests thành Map để truy xuất nhanh theo permissionId
        Map<String, RolePermissionRequest> requestMap = requests.stream()
                .collect(Collectors.toMap(RolePermissionRequest::getPermissionId, r -> r));

        // 3. Xử lý Xóa và Cập nhật các bản ghi đang có
        List<RolePermission> toDelete = new ArrayList<>();
        for (RolePermission current : currentPermissions) {
            String pId = current.getPermission().getId();

            if (requestMap.containsKey(pId)) {
                // Cập nhật scope nếu có thay đổi
                ScopeType newScope = ScopeType.valueOf(requestMap.get(pId).getScopeType());
                if (current.getScopeType() != newScope) {
                    current.setScopeType(newScope);
                }
                // Loại bỏ khỏi map để sau này chỉ còn lại những quyền thực sự "mới"
                requestMap.remove(pId);
            } else {
                // Không có trong request mới -> Đưa vào danh sách xóa
                toDelete.add(current);
            }
        }

        // Thực thi xóa các quyền thừa
        if (!toDelete.isEmpty()) {
            rolePermissionRepository.deleteAll(toDelete);
        }

        // 4. Xử lý Thêm mới (những gì còn lại trong requestMap)
        if (!requestMap.isEmpty()) {
            List<RolePermission> toAdd = requestMap.values().stream().map(req -> {
                Permission permission = permissionRepository.findById(req.getPermissionId())
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Quyền không tồn tại: " + req.getPermissionId()));

                return RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .scopeType(ScopeType.valueOf(req.getScopeType()))
                        .build();
            }).collect(Collectors.toList());

            rolePermissionRepository.saveAll(toAdd);
        }

        // JPA sẽ tự động cập nhật các bản ghi đã thay đổi Scope nhờ cơ chế Dirty Checking
    }

    @Transactional
    public void deleteByRoleId(String roleId) {
        rolePermissionRepository.deleteByRoleId(roleId);
    }
}