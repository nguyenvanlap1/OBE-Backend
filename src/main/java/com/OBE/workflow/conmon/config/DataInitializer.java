package com.OBE.workflow.conmon.config;

import com.OBE.workflow.feature.officer.Officer;
import com.OBE.workflow.feature.officer.OfficerRepository;
import com.OBE.workflow.feature.permission.PermissionRepository;
import com.OBE.workflow.feature.supDepartment.SubDepartmentRepository;
import com.OBE.workflow.feature.auth.AccountRepository;
import com.OBE.workflow.feature.department.Department;
import com.OBE.workflow.feature.department.DepartmentRepository;
import com.OBE.workflow.other_entity_repo.entity.entity.Account;
import com.OBE.workflow.other_entity_repo.entity.entity.AccountRoleSubDepartment;
import com.OBE.workflow.feature.permission.Permission;
import com.OBE.workflow.other_entity_repo.entity.entity.Person;
import com.OBE.workflow.other_entity_repo.entity.entity.Role;
import com.OBE.workflow.other_entity_repo.entity.repository.*;
import com.OBE.workflow.conmon.enums.SystemRoleType;
import com.OBE.workflow.conmon.enums.SystemManagement;
import com.OBE.workflow.feature.permission.PermissionType;
import com.OBE.workflow.feature.supDepartment.SubDepartment;
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
    private final AccountRoleSubDepartmentRepository accountRoleSubDepartmentRepository;
    private final OfficerRepository officerRepository;
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
        // 1. Khởi tạo cấu trúc tổ chức và Role (Giữ nguyên)
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

        // 2. KIỂM TRA VÀ XÓA NẾU USERNAME THAY ĐỔI
        accountRepository.findFirstByIsSystemAccountOrderByUsernameAsc(true)
                .ifPresent(existingAdminAccount -> {
                    if (!existingAdminAccount.getUsername().equals(adminUsername)) {
                        // Lấy Officer liên quan trước khi xóa Account
                        Person oldPerson = existingAdminAccount.getPerson();
                        // Xóa Account và sau đó xóa Officer
                        accountRepository.delete(existingAdminAccount);
                        if (oldPerson instanceof Officer) {
                            officerRepository.delete((Officer) oldPerson);
                        }
                        System.out.println(">>> Đã xóa Admin cũ để cập nhật username mới: " + adminUsername);
                    }
                });

        // 3. TẠO OFFICER VÀ ACCOUNT MỚI (HOẶC LẤY NẾU ĐÃ TRÙNG)
        Officer adminOfficer = officerRepository.findById(adminUsername)
                .orElseGet(() -> officerRepository.save(Officer.builder()
                        .id(adminUsername) // ID của Person là adminUsername
                        .fullName("SYSTEM ADMIN")
                        .gender("MALE")
                        .build()));

        Account adminAccount = accountRepository.findById(adminUsername)
                .orElseGet(() -> {
                    Account newAcc = Account.builder()
                            .password(passwordEncoder.encode(adminPassword))
                            .person(adminOfficer) // @MapsId sẽ lấy adminUsername bỏ vào username
                            .build();
                    newAcc.setSystemAccount(true);
                    newAcc.setEnabled(true);
                    return accountRepository.save(newAcc);
                });

        // Luôn cập nhật password mới nhất từ YAML
        adminAccount.setPassword(passwordEncoder.encode(adminPassword));
        adminAccount = accountRepository.save(adminAccount);

        // 4. Cập nhật quyền cho Account (Giữ nguyên)
        final Account finalAdmin = adminAccount;
        accountRoleSubDepartmentRepository.findByAccountAndRoleAndSubDepartment(finalAdmin, role, subDepartment)
                .orElseGet(() -> accountRoleSubDepartmentRepository.save(AccountRoleSubDepartment.builder()
                        .account(finalAdmin)
                        .role(role)
                        .subDepartment(subDepartment)
                        .build()));
        System.out.println(">>> Đã đồng bộ tài khoản hệ thống thành công.");
    }
}
