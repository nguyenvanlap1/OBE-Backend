package com.OBE.workflow.feature.student_class;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class StudentClassSpecification {

    // 1. Lọc theo mã lớp (LIKE)
    public static Specification<StudentClass> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    // 2. Lọc theo tên lớp (LIKE)
    public static Specification<StudentClass> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty()) ? null :
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // 3. Lọc theo niên khóa (Equal) - Vì mỗi lớp chỉ thuộc 1 SchoolYear
    public static Specification<StudentClass> hasSchoolYear(String yearId) {
        return (root, query, cb) -> (yearId == null || yearId.isEmpty()) ? null :
                cb.equal(root.get("schoolYear").get("id"), yearId);
    }

    // 4. Lọc theo mã CTĐT
    public static Specification<StudentClass> hasEducationProgramId(String epId) {
        return (root, query, cb) -> (epId == null || epId.isEmpty()) ? null :
                cb.equal(root.get("educationProgram").get("id"), epId);
    }

    // 5. Lọc theo tên CTĐT (LIKE)
    public static Specification<StudentClass> hasEducationProgramName(String epName) {
        return (root, query, cb) -> (epName == null || epName.isEmpty()) ? null :
                cb.like(cb.lower(root.get("educationProgram").get("name")), "%" + epName.toLowerCase() + "%");
    }

    // 6. Lọc theo Bộ môn (Join qua EducationProgram)
    public static Specification<StudentClass> hasSubDepartmentId(String subDeptId) {
        return (root, query, cb) -> (subDeptId == null || subDeptId.isEmpty()) ? null :
                cb.equal(root.join("educationProgram").join("subDepartment").get("id"), subDeptId);
    }

    // 7. Lọc theo Khoa (Join 3 tầng: Class -> EP -> SubDept -> Dept)
    public static Specification<StudentClass> hasDepartmentId(String deptId) {
        return (root, query, cb) -> (deptId == null || deptId.isEmpty()) ? null :
                cb.equal(root.join("educationProgram").join("subDepartment").join("department").get("id"), deptId);
    }
}