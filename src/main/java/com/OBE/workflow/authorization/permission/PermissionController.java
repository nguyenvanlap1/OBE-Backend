package com.OBE.workflow.authorization.permission;

import com.OBE.workflow.authorization.permission.enums.IPermission;
import com.OBE.workflow.authorization.permission.enums.PermissionRegistry;
import com.OBE.workflow.authorization.permission.response.PermissionTreeResponse;
import com.OBE.workflow.conmon.dto.ApiResponse;
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
        List<IPermission> allPermissions = PermissionRegistry.getAllPermissions().stream()
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        List<PermissionTreeResponse> tree = groupPermissionsByModule(allPermissions);

        return ResponseEntity.ok(
                ApiResponse.<List<PermissionTreeResponse>>builder()
                        .status(200)
                        .message("Lấy sơ đồ phân quyền thành công")
                        .data(tree)
                        .build()
        );
    }

    private List<PermissionTreeResponse> groupPermissionsByModule(List<IPermission> permissions) {
        return permissions.stream()
                .collect(Collectors.groupingBy(p -> p.getId().split("_")[0]))
                .entrySet().stream()
                .map(entry -> {
                    String prefix = entry.getKey();
                    return PermissionTreeResponse.builder()
                            .id(prefix)
                            .name(getModuleName(prefix)) // Chuyển "USER" -> "Quản lý Người dùng"
                            .description("Các quyền thuộc phân hệ " + prefix)
                            .children(entry.getValue().stream()
                                    .map(this::mapToTreeResponse)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String getModuleName(String prefix) {
        return switch (prefix) {
            case "USER" -> "Quản lý Người dùng";
            case "PROG" -> "Chương trình đào tạo";
            case "COURSE" -> "Quản lý Học phần";
            case "SECTION" -> "Quản lý Lớp học";
            case "ROLE" -> "Cấu hình Hệ thống";
            case "DEPT" -> "Tổ chức Khoa/Bộ môn";
            default -> "Phân hệ " + prefix;
        };
    }

    private PermissionTreeResponse mapToTreeResponse(IPermission p) {
        return PermissionTreeResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .allowedScopes(p.getAllowedScopes().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .children(List.of()) // Cấp thấp nhất không có con
                .build();
    }
}