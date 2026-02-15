package com.OBE.workflow.enums;

import lombok.Getter;

@Getter
public enum SystemRoleType {
    ADMIN("Dành cho admin hệ thống và các người dùng được gán vai trò admin"),
    USER("Dành cho các người dùng còn lại");
    final String description;
    SystemRoleType(String description) {
        this.description = description;
    }
}
