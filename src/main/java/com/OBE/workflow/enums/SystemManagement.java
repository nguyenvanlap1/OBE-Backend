package com.OBE.workflow.enums;
import lombok.Getter;

@Getter
public enum SystemManagement {

    // Một Khoa quản trị duy nhất
    ADMIN_DEPT("DEPT_ADMIN", "Quản trị Hệ thống"),

    // Một Bộ môn quản trị duy nhất
    ADMIN_SUB("SUB_ADMIN", "Vận hành & Bảo trì");

    private final String id;
    private final String name;

    SystemManagement(String id, String nameVi) {
        this.id = id;
        this.name = nameVi;
    }
}
