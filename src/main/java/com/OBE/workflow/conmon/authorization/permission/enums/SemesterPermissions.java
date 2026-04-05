package com.OBE.workflow.conmon.authorization.permission.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum SemesterPermissions implements IPermission {

    SEMESTER_CREATE(
            "SEMESTER_CREATE", "Thêm học kỳ",
            Set.of(ScopeType.TRUONG), // Cấp trường quản lý việc mở học kỳ mới
            Set.of(), "Cho phép tạo mới học kỳ trong hệ thống (VD: Học kỳ 1 - 2024)"
    ),

    SEMESTER_UPDATE(
            "SEMESTER_UPDATE", "Cập nhật học kỳ",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép chỉnh sửa thông tin học kỳ hiện có"
    ),

    SEMESTER_DELETE(
            "SEMESTER_DELETE", "Xóa học kỳ",
            Set.of(ScopeType.TRUONG),
            Set.of(), "Cho phép xóa học kỳ (chỉ thực hiện khi không có lớp học phần liên quan)"
    );

    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
