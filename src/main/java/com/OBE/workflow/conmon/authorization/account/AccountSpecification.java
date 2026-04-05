package com.OBE.workflow.conmon.authorization.account;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AccountSpecification {

    public static Specification<Account> filterAccounts(
            String username,
            String fullName,
            Boolean enabled,
            Boolean isSystem) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo username riêng (Tìm kiếm chính xác hoặc gần đúng tùy bạn, ở đây dùng Like)
            if (username != null && !username.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"));
            }

            // 2. Lọc theo fullName riêng (Join với bảng Person/ca_nhan)
            if (fullName != null && !fullName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("person").get("fullName")),
                        "%" + fullName.toLowerCase() + "%"));
            }

            // 3. Lọc theo trạng thái hoạt động
            if (enabled != null) {
                predicates.add(cb.equal(root.get("enabled"), enabled));
            }

            // 4. Lọc theo tài khoản hệ thống
            if (isSystem != null) {
                predicates.add(cb.equal(root.get("isSystemAccount"), isSystem));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}