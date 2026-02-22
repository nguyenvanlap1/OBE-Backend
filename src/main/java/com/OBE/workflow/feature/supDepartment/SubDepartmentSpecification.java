package com.OBE.workflow.feature.supDepartment;

import org.springframework.data.jpa.domain.Specification;

public class SubDepartmentSpecification {

    // Lọc theo tên bộ môn (không phân biệt hoa thường)
    public static Specification<SubDepartment> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty()) ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // Lọc theo mã bộ môn
    public static Specification<SubDepartment> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    // Lọc theo mã khoa (Truy vấn qua mối quan hệ ManyToOne)
    public static Specification<SubDepartment> hasDepartmentId(String departmentId) {
        return (root, query, cb) -> (departmentId == null || departmentId.isEmpty()) ? null :
                cb.equal(root.get("department").get("id"), departmentId);
    }
}