package com.OBE.workflow.authorization.role.request;

import com.OBE.workflow.authorization.permission.enums.IPermission;
import com.OBE.workflow.authorization.permission.enums.PermissionRegistry;
import com.OBE.workflow.authorization.permission.enums.ScopeType;
import com.OBE.workflow.authorization.role.role_permission.RolePermissionRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RoleRequestDetail {

    @NotBlank(message = "Mã vai trò không được để trống")
    @Size(max = 50, message = "Mã vai trò không được quá 50 ký tự")
    private String id;

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 255, message = "Tên vai trò không được quá 255 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;

    @NotNull(message = "Danh sách quyền không được null")
    @NotEmpty(message = "Vai trò phải có ít nhất một quyền hạn")
    private List<RolePermissionRequest> rolePermissionRequests;

    /**
     * Hàm validate tổng hợp dành cho nghiệp vụ Phân quyền
     */
    public void validateInternal() {
        Set<String> seenPermissionIds = new HashSet<>();

        for (RolePermissionRequest req : this.rolePermissionRequests) {
            // 1. Kiểm tra ID quyền không trùng lặp trong 1 Request
            if (!seenPermissionIds.add(req.getPermissionId())) {
                throw new IllegalArgumentException("Quyền '" + req.getPermissionId() + "' bị gán trùng lặp trong danh sách.");
            }

            // 2. Kiểm tra ScopeType có khớp với Enum không
            ScopeType selectedScope;
            try {
                selectedScope = ScopeType.valueOf(req.getScopeType());
            } catch (Exception e) {
                throw new IllegalArgumentException("Phạm vi '" + req.getScopeType() + "' không hợp lệ.");
            }

            // 3. Tìm định nghĩa gốc từ Registry để check AllowedScopes
            IPermission original = findInRegistry(req.getPermissionId());
            if (original == null) {
                throw new IllegalArgumentException("Mã quyền '" + req.getPermissionId() + "' không tồn tại.");
            }

            if (!original.getAllowedScopes().contains(selectedScope)) {
                throw new IllegalArgumentException(String.format(
                        "Quyền '%s' không được phép gán cho phạm vi '%s'. Chỉ cho phép: %s",
                        original.getName(), selectedScope, original.getAllowedScopes()
                ));
            }
        }
    }

    private IPermission findInRegistry(String permissionId) {
        return PermissionRegistry.getAllPermissions().stream()
                .flatMap(Arrays::stream)
                .filter(p -> p.getId().equals(permissionId))
                .findFirst()
                .orElse(null);
    }
}