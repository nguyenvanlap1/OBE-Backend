package com.OBE.workflow.conmon.authentication;
import com.OBE.workflow.conmon.authorization.account.account_role_sub_department.AccountRoleSubDepartment;
import com.OBE.workflow.conmon.authorization.account.account_role_sub_department.AccountRoleSubDepartmentRepository;
import com.OBE.workflow.conmon.config.JwtUtils;
import com.OBE.workflow.conmon.dto.ApiResponse;
import com.OBE.workflow.conmon.authorization.account.Account;
import com.OBE.workflow.conmon.authorization.account.AccountRepository;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    final private JwtUtils jwtUtils;
    final private PasswordEncoder passwordEncoder;
    final private AccountRepository accountRepository;
    final private AccountRoleSubDepartmentRepository accountRoleSubDepartmentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SecurityService securityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        // 1. Tìm Account và 2. Kiểm tra mật khẩu (Giữ nguyên)
        Account accountUser = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(request.getPassword(), accountUser.getPassword())) {
            throw new BadCredentialsException("Sai tên đăng nhập hoặc mật khẩu");
        }

        // --- BƯỚC MỚI: GIẢI PHÓNG KHỎI BLACKLIST ---
        // Nếu họ đăng nhập lại thành công, ta gỡ bỏ lệnh chặn trong Redis
        securityService.removeUserFromBlacklist(accountUser.getUsername());

        // 3. LẤY DANH SÁCH ROLE + SUBDEPT TỪ REPOSITORY MỚI
        List<AccountRoleSubDepartment> assignments = accountRoleSubDepartmentRepository.findByAccount(accountUser);

        // 4. Chuẩn bị dữ liệu cho JWT và nạp Permission vào Redis
        List<Map<String, String>> userContexts = new ArrayList<>();

        for (AccountRoleSubDepartment assignment : assignments) {
            String roleId = assignment.getRole().getId();
            String subDeptId = assignment.getSubDepartment().getId();
            String facultyId = assignment.getSubDepartment().getDepartment().getId();

            // Thêm vào danh sách để nhét vào JWT Claims
            Map<String, String> ctx = new HashMap<>();
            ctx.put("r", roleId);
            ctx.put("d", subDeptId);
            ctx.put("f", facultyId);
            userContexts.add(ctx);

            // 5. Đồng bộ Permission của Role này vào Redis (nếu chưa có)
            // Xử lý nạp Redis
            String redisKey = "role_auth:" + roleId;
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                List<Map<String, String>> authList = assignment.getRole().getRolePermissions().stream()
                        .map(rp -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("permission", rp.getPermission().getId());
                            map.put("scope", rp.getScopeType().name());
                            return map;
                        }).toList();

                // BIẾN LIST THÀNH STRING JSON TRƯỚC KHI LƯU
                try {
                    String jsonAuthList = objectMapper.writeValueAsString(authList);
                    redisTemplate.opsForValue().set(redisKey, jsonAuthList);
                } catch (JsonProcessingException e) {
                    log.error("Lỗi parse JSON khi lưu Redis cho role: {}", roleId);
                }
            }
        }

        // 6. Tạo Token với Payload chứa "Contexts" thay vì chỉ RoleNames
        // Bạn cần sửa hàm generateToken trong JwtUtils để nhận List<Map<String, String>>
        String token = jwtUtils.generateToken(accountUser.getUsername(), userContexts);

        // 7. Thiết lập Cookie (Giữ nguyên)
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
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
    public ResponseEntity<ApiResponse<UserMeResponse>> getCurrentUser(Authentication authentication) {
        // 1. Kiểm tra xác thực (an toàn)
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Lấy username từ context
        String username = authentication.getName();

        // 3. Gọi Service để lấy Account Entity và chuyển đổi sang DTO
        // Lưu ý: AccountService cần một hàm trả về UserMeResponse
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(()->new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy người dùng với: " + username));

        return ResponseEntity.ok(ApiResponse.<UserMeResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Đã gửi thông tin người dùng")
                .data(UserMeResponse.fromEntity(account))
                .build());
    }
}