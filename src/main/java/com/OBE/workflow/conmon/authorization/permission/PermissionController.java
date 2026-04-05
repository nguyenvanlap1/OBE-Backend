package com.OBE.workflow.conmon.authorization.permission;

import com.OBE.workflow.conmon.authorization.permission.enums.IPermission;
import com.OBE.workflow.conmon.authorization.permission.enums.PermissionRegistry;
import com.OBE.workflow.conmon.authorization.permission.response.PermissionTreeResponse;
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
                // Logic phân nhóm thông minh: kiểm tra các prefix dài/đặc thù trước
                .collect(Collectors.groupingBy(p -> {
                    String id = p.getId();

                    // Danh sách các module có tiền tố chứa dấu gạch dưới (phải check trước split)
                    if (id.startsWith("STUDENT_CLASS_")) return "STUDENT_CLASS";
                    if (id.startsWith("COURSE_VERSION_")) return "COURSE_VERSION";
                    if (id.startsWith("COURSE_SECTION_")) return "COURSE_SECTION";
                    if (id.startsWith("SCHOOL_YEAR_")) return "SCHOOL_YEAR";
                    if (id.startsWith("ED_PROGRAM_")) return "ED_PROGRAM";

                    // Mặc định lấy từ đầu tiên trước dấu gạch dưới
                    return id.split("_")[0];
                }))
                .entrySet().stream()
                .map(entry -> {
                    String prefix = entry.getKey();
                    List<IPermission> modulePermissions = entry.getValue();

                    return PermissionTreeResponse.builder()
                            .id(prefix)
                            .name(getModuleName(prefix))
                            .description("Các quyền thuộc phân hệ " + getModuleName(prefix))
                            .children(modulePermissions.stream()
                                    .map(this::mapToTreeResponse)
                                    .collect(Collectors.toList()))
                            .build();
                })
                // Sắp xếp các Module theo tên để hiển thị trên UI đẹp hơn
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(Collectors.toList());
    }

    private String getModuleName(String prefix) {
        return switch (prefix) {
            case "USER" -> "Quản lý Người dùng";
            case "PROG" -> "Chương trình đào tạo";
            case "COURSE" -> "Quản lý Học phần";
            case "SECTION" -> "Quản lý lớp học phần";
            case "ROLE" -> "Cấu hình Hệ thống";
            case "DEPT" -> "Tổ chức Khoa";
            case "SUBDEPT" -> "Quản lý bộ môn";
            case "COURSE_VERSION" -> "Quản lý học phần";
            case "LECTURER" -> "Quản lý giảng viên";
            case "STUDENT" -> "Quản lý sinh viên";
            case "STUDENT_CLASS" -> "Quản lý lớp sinh viên";
            case "SCHOOL_YEAR" -> "Quản lý niên khóa";
            case "SEMESTER" -> "Quản lý học kì";
            case "ED_PROGRAM" -> "Quản lý chương trình đào tạo";
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