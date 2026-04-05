package com.OBE.workflow.conmon.authorization.account;

import com.OBE.workflow.conmon.authorization.account.account_role_sub_department.AccountRoleSubDepartment;
import com.OBE.workflow.conmon.authorization.account.response.AccountResponseDetail;
import com.OBE.workflow.conmon.authorization.role.Role;
import com.OBE.workflow.conmon.authorization.role.RoleRepository;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.conmon.authorization.account.request.*;
import com.OBE.workflow.conmon.authorization.account.response.AccountResponse;
import com.OBE.workflow.conmon.authorization.account.person.Person;
import com.OBE.workflow.conmon.authorization.account.person.PersonRepository; // Giả sử bạn có repo này
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SubDepartmentRepository subDepartmentRepository;

    // 1. Lọc danh sách tài khoản (Sử dụng Specification đã tạo)
    public Page<AccountResponse> getAccounts(AccountFilterRequest filter, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.filterAccounts(
                filter.getUsername(),
                filter.getFullName(),
                filter.getEnabled(),
                filter.getIsSystemAccount()
        );
        return accountRepository.findAll(spec, pageable).map(AccountResponse::fromEntity);
    }

    /**
     * Lấy chi tiết tài khoản theo username và chuyển đổi sang DTO
     * @param username Tên đăng nhập của tài khoản cần tìm
     * @return DTO chứa thông tin chi tiết tài khoản
     */
    @Transactional(readOnly = true)
    public AccountResponseDetail getAccountDetail(String username) {
        return accountRepository.findByUsername(username)
                .map(AccountResponseDetail::fromEntity)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,"Không tìm thấy tài khoản với username: " + username));
    }

    // 2. Tạo tài khoản mới gắn với một Person
    @Transactional
    public AccountResponseDetail createAccount(AccountRequestDetail request) { // Sử dụng AccountRequestDetail để có danh sách quyền
        // 1. Kiểm tra tài khoản tồn tại
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED, "Tên đăng nhập đã tồn tại");
        }

        // 2. Tìm thông tin cá nhân (Person)
        Person person = personRepository.findById(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy thông tin cá nhân tương ứng"));

        // 3. Khởi tạo Account (mặc định enabled theo request)
        Account account = Account.builder()
                .password(passwordEncoder.encode("123456")) // Hoặc lấy từ request nếu có
                .person(person)
                .build();

        account.setEnabled(request.isEnabled());

        // 4. Kiểm tra trùng lặp quyền trong danh sách gửi lên
        request.checkDuplicateRoles(request.getAccountRoleSubDepartmentResponses());

        // 5. Khởi tạo danh sách quyền (ArrayList) để tránh NullPointerException
        account.setAccountRoleSubDepartments(new ArrayList<>());

        // 6. Map và gán các quyền từ Request
        if (request.getAccountRoleSubDepartmentResponses() != null) {
            request.getAccountRoleSubDepartmentResponses().forEach(roleReq -> {
                Role role = roleRepository.findById(roleReq.getRoleId())
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Role not found: " + roleReq.getRoleId()));

                SubDepartment subDept = subDepartmentRepository.findById(roleReq.getSubDepartmentId())
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "SubDepartment not found: " + roleReq.getSubDepartmentId()));

                AccountRoleSubDepartment assignment = AccountRoleSubDepartment.builder()
                        .account(account)
                        .role(role)
                        .subDepartment(subDept)
                        .build();

                account.getAccountRoleSubDepartments().add(assignment);
            });
        }

        // 7. Lưu và trả về Response
        return AccountResponseDetail.fromEntity(accountRepository.save(account));
    }

    // 3. Admin đổi mật khẩu (Không cần mật khẩu cũ)
    @Transactional
    public void changePasswordByAdmin(AccountChangePasswordForAdmin request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Tài khoản không tồn tại"));

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);
    }

    // 4. Người dùng tự đổi mật khẩu (Cần kiểm tra mật khẩu cũ và confirm)
    @Transactional
    public void changePasswordByUser(AccountChangePasswordForUser request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Tài khoản không tồn tại"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "Mật khẩu cũ không chính xác");
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new AppException(ErrorCode.INVALID_KEY, "Mật khẩu xác nhận không khớp");
        }

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);
    }

    // 5. Khóa/Mở khóa tài khoản
    @Transactional
    public void toggleStatus(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Tài khoản không tồn tại"));
        account.setEnabled(!account.isEnabled());
        accountRepository.save(account);
    }

    // 6. Xóa tài khoản
    @Transactional
    public void deleteAccount(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Tài khoản không tồn tại"));

        // Nếu bạn muốn xóa cả các quyền liên quan trong bảng trung gian trước (nếu không dùng Cascade)
        // accountRoleSubDepartmentRepository.deleteByAccount(account);

        // Kiểm tra nếu là tài khoản hệ thống thì không cho xóa để tránh mất quyền quản trị cao nhất
        if (account.isSystemAccount()) {
            throw new AppException(ErrorCode.INVALID_KEY, "Không thể xóa tài khoản quản trị hệ thống");
        }

        accountRepository.delete(account);
    }

    /**
     * 7. Cập nhật chi tiết tài khoản và danh sách quyền (Roles/Departments)
     */
    @Transactional
    public void updateAccountDetail(AccountRequestDetail request) {
        // B1: Kiểm tra tài khoản tồn tại
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy tài khoản"));

        // B2: Check trùng lặp quyền trong Request trước khi xử lý
        request.checkDuplicateRoles(request.getAccountRoleSubDepartmentResponses());

        // B3: Cập nhật các thông tin cơ bản
        account.setEnabled(request.isEnabled());

        // B4: Cập nhật danh sách quyền (AccountRoleSubDepartment)
        // Xóa sạch danh sách cũ để orphanRemoval = true tự động xóa trong DB
        account.getAccountRoleSubDepartments().clear();

        // B5: Map từ Request DTO sang Entity và add lại vào list của Account
        if (request.getAccountRoleSubDepartmentResponses() != null) {
            request.getAccountRoleSubDepartmentResponses().forEach(roleReq -> {
                // Tìm kiếm Role và SubDepartment để đảm bảo chúng tồn tại
                Role role = roleRepository.findById(roleReq.getRoleId())
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Role not found: " + roleReq.getRoleId()));

                SubDepartment subDept = subDepartmentRepository.findById(roleReq.getSubDepartmentId())
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "SubDepartment not found: " + roleReq.getSubDepartmentId()));

                // Tạo thực thể trung gian mới
                AccountRoleSubDepartment newRoleAssignment = AccountRoleSubDepartment.builder()
                        .account(account)
                        .role(role)
                        .subDepartment(subDept)
                        .build();

                account.getAccountRoleSubDepartments().add(newRoleAssignment);
            });
        }

        accountRepository.save(account);
    }
}