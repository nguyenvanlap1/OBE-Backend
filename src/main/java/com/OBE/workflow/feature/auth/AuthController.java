package com.OBE.workflow.feature.auth;
import com.OBE.workflow.conmon.config.JwtUtils;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.other_entity_repo.entity.entity.Account;
import com.OBE.workflow.other_entity_repo.entity.entity.Role;
import com.OBE.workflow.conmon.enums.SystemRoleType;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.other_entity_repo.entity.service.RoleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    final private JwtUtils jwtUtils;
    final private PasswordEncoder passwordEncoder;
    final private AccountRepository accountRepository;
    final private RoleService roleService;

    // Đọc giá trị từ YAML vào biến
    @Value("${obe.system-admin.username}")
    private String systemAdminUser;

    @Value("${obe.system-admin.password}")
    private String systemAdminPass;

    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        // 1. Tìm account trong DB trước
        Account accountUser = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), accountUser.getPassword())) {
            throw new BadCredentialsException("Sai tên đăng nhập hoặc mật khẩu");
        }

        // 3. Lấy danh sách Role từ bảng trung gian AccountRole
        // Giả sử bạn đã viết hàm findByAccount trong AccountRoleRepository
        List<Role> roles = roleService.getRolesByAccount(accountUser);
        List<String> roleNames = roles.stream().map(Role::getRoleId).toList();

        // 4. Nếu không có role nào, mặc định là USER (hoặc ném lỗi tùy logic của bạn)
        if (roleNames.isEmpty()) {
            roleNames = List.of(SystemRoleType.USER.name());
        }

        // 5. Tạo Token với danh sách Role thực tế
        String token = jwtUtils.generateToken(accountUser.getUsername(), roleNames);

        // 6. Thiết lập Cookie
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đã đăng nhập thành công")
                .build());
    }

    @PostMapping("/api/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Xóa cookie ngay lập tức
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã đăng xuất thành công")
                        .build());
    }

    @GetMapping("/api/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(Authentication authentication) {
        // authentication được Spring Security inject vào sau khi filter check Cookie/Token thành công
        return ResponseEntity.ok(ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Đã gửi thông tin người dùng")
                .data(Map.of(
                "username", authentication.getName(),
                "roles", authentication.getAuthorities(),
                "isAuthenticated", true
                )).build());
    }
}