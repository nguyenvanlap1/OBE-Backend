package com.OBE.workflow.feature.sup_department;

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
    // Bổ sung thêm hàm này vào class SubDepartmentSpecification

    // Lọc theo tên khoa (Truy cập qua join bảng Department)
    public static Specification<SubDepartment> hasDepartmentName(String departmentName) {
        return (root, query, cb) -> {
            if (departmentName == null || departmentName.isEmpty()) {
                return null;
            }
            // Thực hiện join từ SubDepartment sang Department
            // "department" là tên biến đại diện cho quan hệ @ManyToOne trong Entity SubDepartment
            return cb.like(
                    cb.lower(root.join("department").get("name")),
                    "%" + departmentName.toLowerCase() + "%"
            );
        };
    }
}