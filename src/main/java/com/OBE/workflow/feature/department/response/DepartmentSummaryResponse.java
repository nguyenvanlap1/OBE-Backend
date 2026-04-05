package com.OBE.workflow.feature.department.response;

import com.OBE.workflow.feature.department.Department; // Import Entity vào
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSummaryResponse {
    private String id;
    private String name;

    /**
     * Chuyển đổi từ Entity sang DTO rút gọn
     * @param entity Đối tượng Department từ Database
     * @return Đối tượng DTO chỉ chứa ID và Tên
     */
    public static DepartmentSummaryResponse fromEntity(Department entity) {
        if (entity == null) return null;

        return DepartmentSummaryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}