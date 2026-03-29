package com.OBE.workflow.permission.account;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.permission.account.request.*;
import com.OBE.workflow.permission.account.response.AccountResponse;
import com.OBE.workflow.permission.entity.Person;
import com.OBE.workflow.permission.repository.PersonRepository; // Giả sử bạn có repo này
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

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

    // 2. Tạo tài khoản mới gắn với một Person
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED, "Tên đăng nhập đã tồn tại");
        }

        Person person = personRepository.findById(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy thông tin cá nhân tương ứng"));

        Account account = Account.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .person(person)
                .build();

        return AccountResponse.fromEntity(accountRepository.save(account));
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
}