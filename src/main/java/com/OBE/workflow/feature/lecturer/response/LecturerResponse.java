package com.OBE.workflow.feature.lecturer.response;

import com.OBE.workflow.feature.lecturer.Lecturer;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LecturerResponse {
    private String id;
    private String fullName;
    private String gender;
    private List<String> subDepartmentIds;
    private List<String> subDepartmentNames; // Đổi thành số nhiều cho đồng nhất

    public static LecturerResponse fromEntity(Lecturer lecturer) {
        if (lecturer == null) return null;

        // Lấy danh sách IDs của các bộ môn
        List<String> ids = lecturer.getSubDepartments().stream()
                .map(SubDepartment::getId)
                .collect(Collectors.toList());

        // Lấy danh sách Tên của các bộ môn
        List<String> names = lecturer.getSubDepartments().stream()
                .map(SubDepartment::getName) // Giả định field là tenBoMon
                .collect(Collectors.toList());

        return LecturerResponse.builder()
                .id(lecturer.getId())
                .fullName(lecturer.getFullName())
                .gender(lecturer.getGender())
                .subDepartmentIds(ids)
                .subDepartmentNames(names)
                .build();
    }
}