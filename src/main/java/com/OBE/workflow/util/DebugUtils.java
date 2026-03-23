package com.OBE.workflow.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DebugUtils {

    public static void logDeep(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Đăng ký module để xử lý các kiểu ngày tháng (LocalDate, LocalDateTime)
            mapper.registerModule(new JavaTimeModule());
            // Format JSON cho đẹp (có xuống dòng, thụt lề)
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // Không báo lỗi nếu gặp Object rỗng
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            // Ngăn chặn lỗi vòng lặp vô hạn (Infinite Recursion) nếu có quan hệ 2 chiều
            // mapper.enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL);

            String json = mapper.writeValueAsString(obj);

            System.out.println("\n================ DEBUG OBJECT START ================");
            System.out.println("Class: " + (obj != null ? obj.getClass().getName() : "null"));
            System.out.println(json);
            System.out.println("================= DEBUG OBJECT END =================\n");

        } catch (Exception e) {
            System.err.println("Không thể log sâu object: " + e.getMessage());
            // Fallback về toString nếu JSON fail
            System.out.println(obj);
        }
    }
}