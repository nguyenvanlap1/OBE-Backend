package com.OBE.workflow.repository.specification;

import com.OBE.workflow.entity.Department;
import org.springframework.data.jpa.domain.Specification;

public class DepartmentSpecification {

    public static Specification<Department> hasName(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Department> hasId(String id) {
        return (root, query, cb) -> id == null ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }
    // Bạn có thể thêm lọc theo description, v.v.
}