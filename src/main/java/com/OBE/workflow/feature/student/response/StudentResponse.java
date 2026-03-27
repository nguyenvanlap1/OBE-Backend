package com.OBE.workflow.feature.student.response;

import com.OBE.workflow.feature.student.Student;
import lombok.*;

import java.util.HashSet;
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
    private Set<String> studentClassesId; // Trả về danh sách ID như bạn muốn
    private Set<String>  educationProgramId;
    private Set<String> educationProgramName;
    private Set<String> subDepartmentId;
    private Set<String> subDepartmentName;
    private Set<String> departmentId;
    private Set<String> departmentName;

    public static StudentResponse fromEntity(Student student) {
        if (student == null) return null;

        // Khởi tạo Builder
        StudentResponseBuilder builder = StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .gender(student.getGender());

        // Xử lý tập hợp các lớp và các thông tin liên quan
        if (student.getStudentClasses() != null && !student.getStudentClasses().isEmpty()) {
            Set<String> classIds = new HashSet<>();
            Set<String> epIds = new HashSet<>();
            Set<String> epNames = new HashSet<>();
            Set<String> subDeptIds = new HashSet<>();
            Set<String> subDeptNames = new HashSet<>();
            Set<String> deptIds = new HashSet<>();
            Set<String> deptNames = new HashSet<>();

            student.getStudentClasses().forEach(sc -> {
                // 1. Thông tin lớp
                classIds.add(sc.getId());

                // 2. Thông tin Chương trình đào tạo (Education Program)
                if (sc.getEducationProgram() != null) {
                    var ep = sc.getEducationProgram();
                    epIds.add(ep.getId());
                    epNames.add(ep.getName());
                    // 3. Thông tin Bộ môn (SubDepartment)
                    var subDept = ep.getSubDepartment();
                    subDeptIds.add(subDept.getId());
                    subDeptNames.add(subDept.getName());
                    // 4. Thông tin Khoa (Department)
                    var dept = subDept.getDepartment();
                    deptIds.add(dept.getId());
                    deptNames.add(dept.getName());
                }
            });

            builder.studentClassesId(classIds)
                    .educationProgramId(epIds)
                    .educationProgramName(epNames)
                    .subDepartmentId(subDeptIds)
                    .subDepartmentName(subDeptNames)
                    .departmentId(deptIds)
                    .departmentName(deptNames);
        }

        return builder.build();
    }
}