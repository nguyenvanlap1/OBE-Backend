package com.OBE.workflow.feature.lecturer.response;

import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LecturerResponse {
    private String id;
    private String fullName;
    private String gender;
    // Tạm thời chỉ trả về danh sách mã bộ môn (IDs)
    private Set<String> subDepartmentIds;
}