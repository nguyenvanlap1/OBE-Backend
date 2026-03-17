package com.OBE.workflow.feature.course.assessment_component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentCloMappingId implements Serializable {
    private Long assessment; // Tên biến phải khớp với tên field trong Entity
    private Long clo;      // Giả sử CLO id là String
}
