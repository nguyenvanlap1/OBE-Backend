package com.OBE.workflow.feature.education_program;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class EducationProgramSpecification {

    // 2. Lọc theo mã chương trình (Like)
    public static Specification<EducationProgram> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    // 1. Lọc theo tên (LIKE)
    public static Specification<EducationProgram> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty()) ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // Ví dụ: "Đại học", "Cao đẳng", "Thạc sĩ"
    public static Specification<EducationProgram> hasEducationLevel(String level) {
        return (root, query, cb) -> (level == null || level.isEmpty()) ? null :
                cb.like(cb.lower(root.get("educationLevel")), "%" + level.toLowerCase() + "%");
    }

    public static Specification<EducationProgram> hasSubDepartmentId(String subDeptId) {
        return (root, query, cb) -> (subDeptId == null || subDeptId.isEmpty()) ? null :
                cb.like(cb.lower(root.get("subDepartment").get("id")), "%" + subDeptId.toLowerCase() + "%");
    }

    public static Specification<EducationProgram> hasDepartmentId(String deptId) {
        return (root, query, cb) -> {
            if (deptId == null || deptId.isEmpty()) {
                return null;
            }
            // Join từ EducationProgram -> SubDepartment -> Department
            return cb.equal(root.join("subDepartment").join("department").get("id"), deptId);
        };
    }

    // 4. Lọc theo niên khóa (ManyToMany Join)
    // Ví dụ: Tìm CTĐT thuộc khóa "2022"
    public static Specification<EducationProgram> hasSchoolYear(String yearId) {
        return (root, query, cb) -> {
            if (yearId == null || yearId.isEmpty()) return null;
            Join<Object, Object> schoolYearJoin = root.join("schoolYears");
            return cb.equal(schoolYearJoin.get("id"), yearId);
        };
    }
}