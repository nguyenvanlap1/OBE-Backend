package com.OBE.workflow.feature.course;

import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {

    public static Specification<Course> hasId(String id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    public static Specification<Course> hasDefaultName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("defaultName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Course> hasSubDepartmentId(String subDeptId) {
        return (root, query, cb) ->
                subDeptId == null ? null :
                        cb.equal(root.get("subDepartment").get("id"), subDeptId);
    }
}