package com.OBE.workflow.conmon.config.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserPrincipal {
    private String username;
    // Danh sách các cặp Quyền - Đơn vị
    private List<UserRoleContext> userContexts;

    @Getter
    @Builder
    public static class UserRoleContext {
        private String roleId;
        private String subDeptId;
        private String facultyId; // Lưu luôn ID khoa của bộ môn đó để check Scope FACULTY cho nhanh
    }
}