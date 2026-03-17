package com.OBE.workflow.feature.student.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentFilterRequest {
    private String id;
    private String fullName;
    private String gender;

    // Lọc sinh viên theo Chương trình đào tạo (OBE)
    private String educationProgramId;
}