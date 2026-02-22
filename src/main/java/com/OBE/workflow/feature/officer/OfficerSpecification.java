package com.OBE.workflow.feature.officer;

import com.OBE.workflow.feature.officer.Officer;
import org.springframework.data.jpa.domain.Specification;

public class OfficerSpecification {

    // Lọc theo mã cán bộ (Dùng LIKE để tìm kiếm linh hoạt)
    public static Specification<Officer> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    // Lọc theo họ tên
    public static Specification<Officer> hasFullName(String fullName) {
        return (root, query, cb) -> (fullName == null || fullName.isEmpty()) ? null :
                cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    // Lọc theo giới tính (Cũng dùng LIKE nếu bạn muốn gõ "Ma" ra "Male")
    public static Specification<Officer> hasGender(String gender) {
        return (root, query, cb) -> (gender == null || gender.isEmpty()) ? null :
                cb.like(cb.lower(root.get("gender")), "%" + gender.toLowerCase() + "%");
    }
}
