package com.OBE.workflow.feature.department.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private String id;
    private String name;
    private String description;
}
