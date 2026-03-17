package com.OBE.workflow.feature.student.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private String id;
    private String fullName;
    private String gender;
    private Set<String> educationProgramIds; // Trả về danh sách ID như bạn muốn
}