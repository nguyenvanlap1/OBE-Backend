package com.OBE.workflow.feature.assessment_score.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentScoreResponse {
    private Long id;

    // Thông tin sinh viên
    private String studentId;
    private String studentName;

    // Thông tin lớp & học phần
    private String sectionCode;

    // Thông tin cột điểm
    private Long assessmentId;
    private String assessmentName;
    private Double weight; // Tỉ trọng của cột điểm này

    private Double score;
}