package com.OBE.workflow.conmon.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ===== USER =====
    DATA_INTEGRITY_VIOLATION(409, "Dữ liệu không thể xóa do vướng khóa ngoại"),
    USER_EXISTED(409, "Người dùng đã tồn tại"),
    ENTITY_EXISTED(410, "Thực thể đã tồn tại"),
    USER_NOT_FOUND(404, "Không tìm thấy người dùng"),
    ENTITY_NOT_FOUND(404, "Thực thể không tồn tại"),
    USERNAME_INVALID(400, "Tên đăng nhập không hợp lệ"),
    PASSWORD_INVALID(400, "Mật khẩu không hợp lệ"),

    // ===== AUTH =====
    UNAUTHENTICATED(401, "Chưa đăng nhập"),
    FORBIDDEN(403, "Không có quyền truy cập"),

    // ===== VALIDATION =====
    INVALID_REQUEST(400, "Yêu cầu không hợp lệ"),
    MISSING_REQUIRED_FIELD(400, "Thiếu trường bắt buộc"),
    INVALID_FORMAT(400, "Sai định dạng dữ liệu"),
    INVALID_PARAM(400, "Gửi tham số lạ"),
    // ===== SYSTEM =====
    INTERNAL_SERVER_ERROR(500, "Lỗi hệ thống"),
    DATABASE_ERROR(500, "Lỗi cơ sở dữ liệu"),
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa phân loại"),
    INVALID_INPUT(543, "Đầu vào không hợp lệ"),
    INVALID_KEY(5453, "Trùng mã"),
    INVALID_PASSWORD(54354,"Mật khẩu cũ không chính xác" );
    private final int code;
    private final String message;
}
