package com.OBE.workflow.feature.lecturer;

import com.OBE.workflow.feature.supDepartment.SubDepartment;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class LecturerSpecification {

    public static Specification<Lecturer> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    public static Specification<Lecturer> hasFullName(String fullName) {
        return (root, query, cb) -> (fullName == null || fullName.isEmpty()) ? null :
                cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<Lecturer> hasGender(String gender) {
        return (root, query, cb) -> (gender == null || gender.isEmpty()) ? null :
                cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
    }

    // --- MỚI: Lọc giảng viên theo mã bộ môn ---
    public static Specification<Lecturer> hasSubDepartmentId(String subDepartmentId) {
        return (root, query, cb) -> {
            if (subDepartmentId == null || subDepartmentId.isEmpty()) return null;

            Join<Lecturer, SubDepartment> subDeptJoin = root.join("subDepartments");

            // LIKE '%value%'
            return cb.like(
                    cb.lower(subDeptJoin.get("id")),
                    "%" + subDepartmentId.toLowerCase() + "%"
            );
        };
    }
}