package com.OBE.workflow.authorization.role;

import com.OBE.workflow.authorization.account.Account;
import com.OBE.workflow.authorization.account.account_role_sub_department.AccountRoleSubDepartment;
import com.OBE.workflow.authorization.account.account_role_sub_department.AccountRoleSubDepartmentRepository;
import com.OBE.workflow.authorization.role.request.RoleFilterRequest;
import com.OBE.workflow.authorization.role.request.RoleRequestDetail;
import com.OBE.workflow.authorization.role.response.RoleResponse;
import com.OBE.workflow.authorization.role.response.RoleResponseDetail;
import com.OBE.workflow.authorization.role.role_permission.RolePermissionService;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RolePermissionService rolePermissionService;
    private final AccountRoleSubDepartmentRepository accountRoleSubDepartmentRepository;

    @Transactional(readOnly = true)
    public Page<RoleResponse> getRoles(Pageable pageable, RoleFilterRequest filter) {
        // 1. Xây dựng bộ lọc động
        Specification<Role> spec = Specification
                .where(RoleSpecification.hasId(filter.getId()))
                .and(RoleSpecification.hasName(filter.getName()));

        // 2. Truy vấn từ Database với phân trang
        Page<Role> rolePage = roleRepository.findAll(spec, pageable);

        // 3. Map từ Entity sang DTO để trả về Frontend
        return rolePage.map(RoleResponse::fromEntity);
    }


    @Transactional(readOnly = true)
    public RoleResponseDetail getRoleDetail(String id) {
        // Tìm entity, nếu không có quăng lỗi 404 (nên dùng Custom Exception)
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò có mã: " + id));

        // Sử dụng hàm static mapping đã có trong RoleResponseDetail
        return RoleResponseDetail.fromEntity(role);
    }

    @Transactional
    public RoleResponseDetail createRole(RoleRequestDetail request) {
        // 1. Validate nghiệp vụ (Enum, Trùng lặp, Allowed Scopes)
        request.validateInternal();

        if (roleRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Mã vai trò đã tồn tại");
        }

        // 2. Lưu thông tin Role cơ bản
        Role role = Role.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Role savedRole = roleRepository.save(role);

        // 3. Lưu chi tiết các quyền hạn thông qua RolePermissionService
        rolePermissionService.saveRolePermissions(savedRole, request.getRolePermissionRequests());

        return RoleResponseDetail.fromEntity(savedRole);
    }

    @Transactional
    public RoleResponseDetail updateRole(String id, RoleRequestDetail request) {
        request.validateInternal();

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy vai trò"));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Cập nhật lại toàn bộ danh sách quyền (Xóa cũ - Thêm mới)
        rolePermissionService.updateRolePermissions(role, request.getRolePermissionRequests());

        return RoleResponseDetail.fromEntity(roleRepository.save(role));
    }

    @Transactional
    public void deleteRole(String id) {
        if (!roleRepository.existsById(id)) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy vai trò để xóa");
        }
        // Lưu ý: Cần xóa RolePermission trước nếu không dùng CascadeType.ALL
        rolePermissionService.deleteByRoleId(id);
        roleRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    public List<Role> getRolesByAccount(Account account) {
        List<AccountRoleSubDepartment> accountRoleSubDepartments = accountRoleSubDepartmentRepository.findByAccount(account);
        return accountRoleSubDepartments.stream()
                .map(AccountRoleSubDepartment::getRole)
                .distinct()
                .collect(Collectors.toList());
    }
}