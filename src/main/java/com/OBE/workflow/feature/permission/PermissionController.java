package com.OBE.workflow.feature.permission;

import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.feature.permission.response.PermissionTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<PermissionTreeResponse>>> getPermissionTree() {
        // Lấy các quyền gốc (những quyền không phải là con của bất kỳ quyền nào khác)
        // Hoặc đơn giản là lấy các quyền "MANAGE" vì chúng chứa "VIEW"

        List<PermissionTreeResponse> tree = Arrays.stream(PermissionType.values())
                // Chỉ lấy các quyền gốc để tránh lặp lại (ví dụ các quyền MANAGE)
                .filter(this::isRootPermission)
                .map(this::mapToTreeResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.<List<PermissionTreeResponse>>builder()
                        .status(200)
                        .message("Lấy sơ đồ phân quyền thành công")
                        .data(tree)
                        .build()
        );
    }

    private PermissionTreeResponse mapToTreeResponse(PermissionType type) {
        return PermissionTreeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .description(type.getDescription())
                .allowedScopes(type.getAllowedScopes().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .children(type.getChildPermissionTypes().stream()
                        .map(this::mapToTreeResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    // Logic kiểm tra xem quyền này có phải là quyền cấp cao nhất không
    private boolean isRootPermission(PermissionType p) {
        return Arrays.stream(PermissionType.values())
                .noneMatch(parent -> parent.getChildPermissionTypes().contains(p));
    }
}
