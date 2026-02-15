package com.OBE.workflow.config;

import com.OBE.workflow.repository.*;
import com.OBE.workflow.entity.*;
import com.OBE.workflow.enums.SystemRoleType;
import com.OBE.workflow.enums.SystemManagement;
import com.OBE.workflow.enums.PermissionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final DepartmentRepository departmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private final PermissionRepository permissionRepository;

    // Đọc username từ file YAML, nếu không thấy thì mặc định là "admin"
    @Value("${obe.system-admin.username:admin}")
    private String adminUsername;

    // Đọc password từ file YAML
    @Value("${obe.system-admin.password:admin123}")
    private String adminPassword;

    @Override
    @Transactional // Thêm Transactional để đảm bảo tính toàn vẹn khi xóa/tạo
    public void run(ApplicationArguments args) {
        initialAdmin();
        initialPermissions();
    }

    private void initialPermissions() {
        Arrays.stream(PermissionType.values()).forEach(
                (permissionType) -> {
                    if(!permissionRepository.existsById(permissionType.getId())){
                        permissionRepository.save(Permission.builder()
                                .id(permissionType.getId())
                                .permissionType(permissionType)
                                .build());
                    }
                }
        );
    }

    private void initialAdmin() {
        // 1. Khởi tạo cấu trúc tổ chức và Role (Giữ nguyên logic của bạn)
        Department department = departmentRepository.findById(SystemManagement.ADMIN_DEPT.getId())
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .id(SystemManagement.ADMIN_DEPT.getId())
                        .name(SystemManagement.ADMIN_DEPT.getName())
                        .build()));

        SubDepartment subDepartment = subDepartmentRepository.findById(SystemManagement.ADMIN_SUB.getId())
                .orElseGet(() -> subDepartmentRepository.save(SubDepartment.builder()
                        .id(SystemManagement.ADMIN_SUB.getId())
                        .name(SystemManagement.ADMIN_SUB.getName())
                        .department(department)
                        .build()));

        Role role = roleRepository.findById(SystemRoleType.ADMIN.name())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(SystemRoleType.ADMIN.name())
                        .name(SystemRoleType.ADMIN.name())
                        .build()));

        // 2. XỬ LÝ ĐẶC BIỆT CHO ACCOUNT (VÌ USERNAME LÀ ID)
        Account admin = accountRepository.findFirstByIsSystemAccountOrderByUsernameAsc(true)
                //nếu tìm thấy
                .map(existingAdmin -> {
                    // Nếu đổi username trong YAML khác với DB
                    if (!existingAdmin.getUsername().equals(adminUsername)) {
                        // Xóa bản ghi cũ vì ID đã thay đổi
                        accountRepository.delete(existingAdmin);
                        return Account.builder()
                                .username(adminUsername)
                                .password(passwordEncoder.encode(adminPassword))
                                .build(); // Tạo mới instance
                    }
                    return existingAdmin;
                })
                .orElseGet(() -> Account.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .build());

        // Cập nhật thông tin mới nhất từ YAML
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setSystemAccount(true);
        admin = accountRepository.save(admin);

        // 3. Cập nhật quyền cho Account mới/vừa cập nhật
        final Account finalAdmin = admin; // Biến final để dùng trong lambda
        accountRoleRepository.findByAccountAndRoleAndScope(finalAdmin, role, subDepartment)
                .orElseGet(() -> accountRoleRepository.save(AccountRole.builder()
                        .account(finalAdmin)
                        .role(role)
                        .scope(subDepartment)
                        .build()));

        System.out.println(">>> Đã đồng bộ tài khoản hệ thống với username: " + adminUsername);
    }
}
