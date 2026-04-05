package com.OBE.workflow.conmon.authorization.permission.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum EducationProgramPermissions implements IPermission {

    // --- Quản lý chung danh mục CTĐT ---
    ED_PROGRAM_CREATE(
            "ED_PROGRAM_CREATE", "Tạo chương trình đào tạo",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA, ScopeType.BO_MON),
            Set.of(), "Cho phép tạo mới khung chương trình đào tạo"
    ),

    ED_PROGRAM_UPDATE(
            "ED_PROGRAM_UPDATE", "Cập nhật chương trình đào tạo",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA, ScopeType.BO_MON),
            Set.of(), "Cho phép chỉnh sửa thông tin cơ bản của chương trình đào tạo"
    ),

    ED_PROGRAM_DELETE(
            "ED_PROGRAM_DELETE", "Xóa chương trình đào tạo",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA, ScopeType.BO_MON),
            Set.of(), "Cho phép xóa chương trình đào tạo (chỉ khi chưa có dữ liệu sinh viên/lớp học)"
    ),

    ED_PROGRAM_MANAGE_COURSES(
            "ED_PROGRAM_MANAGE_COURSES", "Quản lý học phần trong CTĐT",
            Set.of(ScopeType.TRUONG, ScopeType.KHOA, ScopeType.BO_MON),
            Set.of(), "Cho phép thêm, xóa hoặc thay đổi khối kiến thức của học phần trong chương trình"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<ScopeType> getAllowedScopes() {
        return this.allowedScopes;
    }

    @Override
    public Set<IPermission> getChildPermissions() {
        return this.childPermissions;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}