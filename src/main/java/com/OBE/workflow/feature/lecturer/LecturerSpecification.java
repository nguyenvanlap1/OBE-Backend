package com.OBE.workflow.feature.lecturer;

import com.OBE.workflow.feature.sup_department.SubDepartment;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    public static Specification<Lecturer> hasSubDepartmentId(String subDepartmentId) {
        return (root, query, cb) -> {
            if (subDepartmentId == null || subDepartmentId.trim().isEmpty()) return null;

            // Tránh nhân bản dòng dữ liệu
            assert query != null;
            query.distinct(true);

            // Sử dụng Join để lọc
            Join<Lecturer, SubDepartment> subDeptJoin = root.join("subDepartments");

            // Thực hiện Fetch để tránh N+1 nếu không phải câu query count
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("subDepartments", JoinType.LEFT);
            }

            return cb.equal(subDeptJoin.get("id"), subDepartmentId);
        };
    }
}