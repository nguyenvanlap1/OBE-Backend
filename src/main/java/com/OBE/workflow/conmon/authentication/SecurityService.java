package com.OBE.workflow.conmon.authentication;

import com.OBE.workflow.conmon.authorization.role.Role;
import com.OBE.workflow.conmon.config.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ROLE_AUTH_PREFIX = "role_auth:";
    private static final String BLACKLIST_PREFIX = "blacklist_user:";

    // ========================================================================
    // 1. QUẢN LÝ ROLE CACHE TRÊN REDIS
    // ========================================================================

    /**
     * Thêm hoặc cập nhật danh sách quyền của Role vào Redis (Dạng JSON String)
     */
    public void saveOrUpdateRoleCache(Role role) {
        String redisKey = ROLE_AUTH_PREFIX + role.getId();

        // Chuyển đổi RolePermission entity sang Map để Serialization
        List<Map<String, String>> authList = role.getRolePermissions().stream()
                .map(rp -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("permission", rp.getPermission().getId());
                    map.put("scope", rp.getScopeType().name());
                    return map;
                }).toList();

        try {
            String jsonAuthList = objectMapper.writeValueAsString(authList);
            // set() sẽ tự động ghi đè (overwrite) nếu key đã tồn tại
            redisTemplate.opsForValue().set(redisKey, jsonAuthList);
            log.info(">>>> [REDIS] Đã cập nhật cache cho Role: {}", role.getId());
        } catch (JsonProcessingException e) {
            log.error(">>>> [REDIS ERROR] Lỗi parse JSON cho Role {}: {}", role.getId(), e.getMessage());
        }
    }

    /**
     * Xóa cache của 1 Role cụ thể (Dùng khi Delete Role)
     */
    public void deleteRoleCache(String roleId) {
        String key = ROLE_AUTH_PREFIX + roleId;
        redisTemplate.delete(key);
        log.info(">>>> [REDIS] Đã xóa cache cho Role ID: {}", roleId);
    }

    /**
     * Xóa toàn bộ Role cache hiện có (Dùng cho bảo trì hệ thống)
     */
    public void deleteAllRoleCaches() {
        Set<String> keys = redisTemplate.keys(ROLE_AUTH_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info(">>>> [REDIS] Đã dọn dẹp toàn bộ Role Auth cache. Số lượng: {}", keys.size());
        }
    }

    // ========================================================================
    // 2. QUẢN LÝ BLACKLIST (LOGOUT CƯỠNG BỨC)
    // ========================================================================

    /**
     * Đưa người dùng vào danh sách đen dựa trên thời gian sống tối đa của JWT
     */
    public void forceLogoutUser(String username) {
        String key = BLACKLIST_PREFIX + username;
        // Lấy thời gian hết hạn tối đa từ config để chặn triệt để
        long ttlSeconds = jwtUtils.getMaxExpiration() / 1000;
        if (ttlSeconds <= 0) ttlSeconds = 3600; // Mặc định 1h nếu không lấy được config

        redisTemplate.opsForValue().set(key, "logged_out", ttlSeconds, TimeUnit.SECONDS);
        log.info(">>>> [SECURITY] Admin đã đá {} ra khỏi hệ thống trong {} giây", username, ttlSeconds);
    }

    /**
     * Giải phóng người dùng khỏi danh sách đen (Dùng khi họ login lại thành công)
     */
    public void removeUserFromBlacklist(String username) {
        redisTemplate.delete(BLACKLIST_PREFIX + username);
        log.info(">>>> [SECURITY] Đã gỡ bỏ người dùng {} khỏi danh sách đen", username);
    }

    /**
     * Kiểm tra trạng thái bị chặn của username (Sử dụng trong Filter)
     */
    public boolean isUserBlacklisted(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + username));
    }

    /**
     * Lấy danh sách tất cả username đang bị Admin chặn
     */
    public Set<String> getAllBlacklistedUsers() {
        Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
        if (keys == null) return Set.of();
        return keys.stream()
                .map(key -> key.substring(BLACKLIST_PREFIX.length()))
                .collect(Collectors.toSet());
    }

    // ========================================================================
    // 3. QUẢN LÝ COOKIE & CLIENT SIDE
    // ========================================================================

    /**
     * Xóa cookie ở phía Client bằng cách set MaxAge = 0
     */
    public void clearAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Đổi thành true nếu chạy HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info(">>>> [COOKIE] Đã gửi yêu cầu xóa accessToken cookie");
    }
}