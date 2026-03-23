package com.OBE.workflow.feature.school_year;

import org.springframework.data.jpa.domain.Specification;

public class SchoolYearSpecification {
    public static Specification<SchoolYear> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty())
                ? cb.conjunction()
                : cb.like(root.get("id"), "%" + id + "%");
    }
}
