package com.OBE.workflow.conmon.authorization.permission;

import com.OBE.workflow.conmon.authorization.permission.enums.ScopeType;
import com.OBE.workflow.conmon.enums.SystemRoleType;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.conmon.config.dto.UserPrincipal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("ps")
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Khai báo ObjectMapper để giải mã JSON từ Redis
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean hasPermission(String requiredPerm, String targetUnitId, String targetFacultyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn");
        }

        UserPrincipal currentUser;
        Object principal = auth.getPrincipal();

        // 1. Trích xuất thông tin User hiện tại (từ Custom Principal hoặc từ JWT)
        if (principal instanceof UserPrincipal) {
            currentUser = (UserPrincipal) principal;
        } else if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            // LẤY CLAIM "userContexts" (Đã đồng bộ với AuthController)
            List<Map<String, String>> rawContexts = jwt.getClaim("ctx");

            if (rawContexts == null) {
                throw new AppException(ErrorCode.FORBIDDEN, "Token thiếu thông tin quyền hạn (ctx)");
            }

            // Map lại dữ liệu từ Token vào List Context
            List<UserPrincipal.UserRoleContext> contexts = rawContexts.stream().map(map ->
                    UserPrincipal.UserRoleContext.builder()
                            .roleId(map.get("r"))
                            .subDeptId(map.get("d"))
                            .facultyId(map.get("f"))
                            .build()
            ).toList();

            currentUser = UserPrincipal.builder()
                    .username(jwt.getSubject())
                    .userContexts(contexts)
                    .build();
        } else {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Định dạng Principal không được hỗ trợ");
        }

        if (currentUser.getUserContexts() == null || currentUser.getUserContexts().isEmpty()) {
            throw new AppException(ErrorCode.FORBIDDEN, "Tài khoản của bạn chưa được cấp vai trò nào");
        }

        boolean hasRightAction = false;

        // 2. Duyệt qua từng Role-Context để kiểm tra quyền
        for (UserPrincipal.UserRoleContext context : currentUser.getUserContexts()) {

            // Nếu là ADMIN hệ thống thì auto-pass
            if (SystemRoleType.ADMIN.name().equals(context.getRoleId())) {
                return true;
            }

            String redisKey = "role_auth:" + context.getRoleId();
            Object cachedValue = redisTemplate.opsForValue().get(redisKey);

            if (cachedValue == null) continue;

            List<Map<String, String>> authList;
            try {
                // Đọc String JSON từ Redis và chuyển thành List<Map>
                String json = (String) cachedValue;
                authList = objectMapper.readValue(json, new TypeReference<List<Map<String, String>>>() {});
            } catch (Exception e) {
                log.error("Lỗi Deserialization tại key: {}. Đã xóa key để nạp lại.", redisKey);
                redisTemplate.delete(redisKey);
                continue;
            }

            // 3. Kiểm tra từng Permission trong danh sách của Role
            for (Map<String, String> authData : authList) {
                if (requiredPerm.equals(authData.get("permission"))) {
                    hasRightAction = true;
                    String scope = authData.get("scope");

                    // Cấp TRƯỜNG: Có quyền trên mọi đơn vị
                    if (ScopeType.TRUONG.name().equals(scope)) return true;

                    // Cấp KHOA: Kiểm tra FacultyId có khớp với mục tiêu không
                    if (ScopeType.KHOA.name().equals(scope)) {
                        if (context.getFacultyId() != null && context.getFacultyId().equals(targetFacultyId)) {
                            return true;
                        }
                    }

                    // Cấp BỘ MÔN: Kiểm tra SubDeptId có khớp với mục tiêu không
                    if (ScopeType.BO_MON.name().equals(scope)) {
                        if (context.getSubDeptId() != null && context.getSubDeptId().equals(targetUnitId)) {
                            return true;
                        }
                    }
                }
            }
        }

        // 4. Xử lý thông báo lỗi chi tiết
        if (!hasRightAction) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền: " + requiredPerm);
        } else {
            throw new AppException(ErrorCode.FORBIDDEN,
                    String.format("Hành động bị từ chối. Phạm vi quản lý của bạn không bao gồm đơn vị [BM: %s, Khoa: %s]",
                            targetUnitId, targetFacultyId));
        }
    }
}