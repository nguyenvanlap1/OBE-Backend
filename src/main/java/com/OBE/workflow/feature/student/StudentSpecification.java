package com.OBE.workflow.feature.student;

import com.OBE.workflow.feature.education_program.EducationProgram;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {

    public static Specification<Student> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    public static Specification<Student> hasFullName(String fullName) {
        return (root, query, cb) -> (fullName == null || fullName.isEmpty()) ? null :
                cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<Student> hasGender(String gender) {
        return (root, query, cb) -> (gender == null || gender.isEmpty()) ? null :
                cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
    }

    // Lọc sinh viên theo mã chương trình đào tạo (Many-to-Many Join)
    public static Specification<Student> hasEducationProgramId(String educationProgramId) {
        return (root, query, cb) -> {
            if (educationProgramId == null || educationProgramId.isEmpty()) return null;

            // Thực hiện Join từ Student sang Set<EducationProgram>
            Join<Student, EducationProgram> programJoin = root.join("educationPrograms");

            return cb.like(
                    cb.lower(programJoin.get("id")),
                    "%" + educationProgramId.toLowerCase() + "%"
            );
        };
    }
}