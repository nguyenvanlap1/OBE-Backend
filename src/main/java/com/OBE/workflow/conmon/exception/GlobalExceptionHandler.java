package com.OBE.workflow.conmon.exception;

import com.OBE.workflow.conmon.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Chỉ dành cho lỗi 401 (Sai tài khoản, chưa đăng nhập)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(Exception e) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // 2. Chỉ dành cho lỗi 403 (Đăng nhập rồi nhưng không có quyền vào Dashboard)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(Exception e) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Dữ liệu không hợp lệ"
                : ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();

        return buildResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        return buildResponseEntity(HttpStatus.valueOf(errorCode.getCode()), e.getMessage());
    }

    // Bắt lỗi khi Jackson parse JSON sai định dạng (ví dụ ngày 31/04 hoặc sai format yyyy-MM-dd)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handlingHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        exception.printStackTrace();
        String message = "Dữ liệu đầu vào không hợp lệ hoặc sai định dạng.";

        Throwable cause = exception.getCause();
        // Duyệt tìm nguyên nhân gốc rễ (Root Cause)
        while (cause != null) {
            // Kiểm tra nếu lỗi xuất phát từ việc parse ngày tháng không tồn tại
            if (cause instanceof java.time.format.DateTimeParseException) {
                message = "Ngày tháng không hợp lệ hoặc không tồn tại (Ví dụ: tháng 4 không có ngày 31).";
                break;
            }
            // Kiểm tra nếu lỗi do sai kiểu dữ liệu (String sang Number, String sang Date...)
            if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
                message = "Định dạng dữ liệu không đúng (Ví dụ: sai định dạng ngày yyyy-MM-dd).";
                break;
            }
            cause = cause.getCause();
        }
        System.out.println(message);

        // Sử dụng helper method buildResponseEntity của bạn để đồng bộ format
        return buildResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    // 3. Bắt tất cả các lỗi chưa được định nghĩa ở trên (Lỗi 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleDefaultException(Exception e) {
        // In lỗi ra log để bạn còn biết đường mà sửa (debug)
        e.printStackTrace();
        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Đã có lỗi hệ thống xảy ra: " + e.getMessage()
        );
    }

    // 3. Hàm dùng chung để không phải gõ Builder lặp đi lặp lại (Helper Method)
    private ResponseEntity<ApiResponse<Object>> buildResponseEntity(HttpStatus status, String message) {
        ApiResponse<Object> response = ApiResponse.builder()
                .status(status.value())
                .message(message)
                .data(null) // Lỗi thì không có data
                .build();
        return ResponseEntity.status(status).body(response);
    }
}