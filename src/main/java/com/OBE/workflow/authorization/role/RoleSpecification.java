package com.OBE.workflow.authorization.role;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class RoleSpecification {

    public static Specification<Role> hasId(String id) {
        return (root, query, cb) ->
                !StringUtils.hasText(id) ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Role> hasName(String name) {
        return (root, query, cb) ->
                !StringUtils.hasText(name) ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}