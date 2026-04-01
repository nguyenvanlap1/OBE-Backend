package com.OBE.workflow.authorization.account;

import com.OBE.workflow.authorization.account.response.AccountResponseDetail;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.authorization.account.request.*;
import com.OBE.workflow.authorization.account.response.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 1. Lọc và phân trang
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccountResponse>>> getAccounts(
            AccountFilterRequest filter,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<AccountResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(accountService.getAccounts(filter, pageable))
                .build());
    }

    // 2. Tạo tài khoản
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponseDetail>> create(
            @Valid @RequestBody AccountRequestDetail request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AccountResponseDetail>builder() // Sửa ở đây
                .status(HttpStatus.CREATED.value())
                .message("Tạo tài khoản thành công")
                .data(accountService.createAccount(request)) // Lúc này Service cũng phải trả về Detail
                .build());
    }

    // 3. Admin đổi mật khẩu
    @PutMapping("/change-password/admin")
    public ResponseEntity<ApiResponse<Void>> changePasswordByAdmin(
            @Valid @RequestBody AccountChangePasswordForAdmin request) {
        accountService.changePasswordByAdmin(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Admin đã đổi mật khẩu thành công")
                .build());
    }

    // 4. User tự đổi mật khẩu
    @PutMapping("/change-password/user")
    public ResponseEntity<ApiResponse<Void>> changePasswordByUser(
            @Valid @RequestBody AccountChangePasswordForUser request) {
        accountService.changePasswordByUser(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Đổi mật khẩu thành công")
                .build());
    }

    // 5. Khóa/Mở khóa (Toggle)
    @PatchMapping("/{username}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable("username") String username) {
        accountService.toggleStatus(username);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật trạng thái thành công")
                .build());
    }

    // 6. Xóa tài khoản
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("username") String username) {
        accountService.deleteAccount(username);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xóa tài khoản thành công")
                .build());
    }

    // 7. Lấy chi tiết tài khoản (Bao gồm danh sách quyền/bộ môn)
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<AccountResponseDetail>> getAccountDetail(
            @PathVariable("username") String username) {
        return ResponseEntity.ok(ApiResponse.<AccountResponseDetail>builder()
                .status(HttpStatus.OK.value())
                .data(accountService.getAccountDetail(username))
                .build());
    }

    // 8. Cập nhật chi tiết tài khoản và danh sách phân quyền
    @PutMapping("/detail")
    public ResponseEntity<ApiResponse<Void>> updateAccountDetail(
            @Valid @RequestBody AccountRequestDetail request) {
        accountService.updateAccountDetail(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật thông tin và phân quyền thành công")
                .build());
    }
}
