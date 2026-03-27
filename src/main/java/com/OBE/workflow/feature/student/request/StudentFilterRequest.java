package com.OBE.workflow.feature.student.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StudentFilterRequest {
    private String id;
    private String fullName;
    private String gender;
    private Set<String> studentClassesId; // Trả về danh sách ID như bạn muốn
    private Set<String>  educationProgramId;
    private Set<String> educationProgramName;
    private Set<String> subDepartmentId;
    private Set<String> subDepartmentName;
    private Set<String> departmentId;
    private Set<String> departmentName;
}