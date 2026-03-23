package com.OBE.workflow.feature.course_version.response;

import com.OBE.workflow.feature.course_version.CourseVersion;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseVersionResponse {

    // --- Thông tin từ CourseVersion (Phiên bản học phần) ---
    private Integer versionNumber;    // so_thu_tu_phien_ban
    private Integer credits;          // so_tin_chi
    private LocalDate fromDate;       // ap_dung_tu_ngay
    private LocalDate toDate;         // ap_dung_den_ngay (null nếu đang active)

    // --- Thông tin từ Course (Học phần gốc) ---
    private String courseId;          // ma_hoc_phan (VD: CT101)
    private String courseName;        // ten_hoc_phan

    // --- Thông tin từ SubDepartment (Bộ môn) ---
    private String subDepartmentId;   // ma_bo_mon
    // (Lưu ý: Bạn có thể thêm subDepartmentName nếu Entity SubDepartment có trường đó)

    // --- Thông tin từ Department (Khoa) ---
    private String departmentId;      // ma_khoa
    private String departmentName;    // ten_khoa
    private String departmentDescription; // mieu_ta_khac

    public static CourseVersionResponse fromEntity(CourseVersion entity) {
        if (entity == null) return null;

        // Truy xuất các đối tượng liên quan (Theo cấu trúc Entity của bạn)
        // Lưu ý: Course và SubDepartment thường là non-nullable trong thiết kế của bạn
        var course = entity.getCourse();
        var subDept = course.getSubDepartment();
        var dept = subDept.getDepartment();

        return CourseVersionResponse.builder()
                // Dữ liệu trực tiếp từ CourseVersion
                .versionNumber(entity.getVersionNumber())
                .credits(entity.getCredits())
                .fromDate(entity.getFromDate())
                .toDate(entity.getToDate()) // Có thể null nếu đang hiệu lực
                // Dữ liệu từ Course (Học phần)
                .courseId(course.getId())
                .courseName(entity.getName())
                // Dữ liệu từ SubDepartment (Bộ môn)
                .subDepartmentId(subDept.getId())
                // Dữ liệu từ Department (Khoa)
                .departmentId(dept.getId())
                .departmentName(dept.getName())
                .departmentDescription(dept.getDescription())
                .build();
    }
}