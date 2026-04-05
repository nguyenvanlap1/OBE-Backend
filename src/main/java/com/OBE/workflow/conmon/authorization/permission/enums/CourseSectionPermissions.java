package com.OBE.workflow.conmon.authorization.permission.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum CourseSectionPermissions implements IPermission {

    SECTION_CREATE(
            "SECTION_CREATE", "Tạo lớp học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép tạo mới một lớp học phần và đồng bộ khung điểm"
    ),

    SECTION_WRITE(
            "SECTION_WRITE", "Cập nhật lớp học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Cho phép chỉnh sửa thông tin lớp, giảng viên và thêm/xóa sinh viên, lên điểm cho sinh viên"
    ),

    SECTION_DELETE(
            "SECTION_DELETE", "Xóa lớp học phần",
            Set.of(ScopeType.BO_MON, ScopeType.KHOA, ScopeType.TRUONG),
            Set.of(), "Gỡ bỏ hoàn toàn lớp học phần khỏi hệ thống"
    );
    private final String id;
    private final String name;
    private final Set<ScopeType> allowedScopes;
    private final Set<IPermission> childPermissions;
    private final String description;
}
