package com.OBE.workflow.feature.student;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class StudentSpecification {

    // 1. Lọc theo mã sinh viên (LIKE)
    public static Specification<Student> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("id")), "%" + id.toLowerCase() + "%");
    }

    // 2. Lọc theo tên sinh viên (LIKE)
    public static Specification<Student> hasFullName(String fullName) {
        return (root, query, cb) -> (fullName == null || fullName.isEmpty()) ? null :
                cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    // 3. Lọc theo giới tính (Equal)
    public static Specification<Student> hasGender(String gender) {
        return (root, query, cb) -> (gender == null || gender.isEmpty()) ? null :
                cb.equal(root.get("gender"), gender);
    }

    // 4. Lọc theo danh sách ID lớp (Sử dụng IN)
    public static Specification<Student> hasStudentClasses(Set<String> classIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(classIds)) return null;
            query.distinct(true);
            return root.join("studentClasses").get("id").in(classIds);
        };
    }

    // 5. Lọc theo mã CTĐT (Join qua StudentClass)
    public static Specification<Student> hasEducationProgramId(Set<String> epIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(epIds)) return null;
            query.distinct(true);
            return root.join("studentClasses")
                    .join("educationProgram")
                    .get("id").in(epIds);
        };
    }

    // 6. Lọc theo tên CTĐT (Join & LIKE - hỗ trợ Set tên)
    public static Specification<Student> hasEducationProgramNames(Set<String> epNames) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(epNames)) return null;
            query.distinct(true);
            // Với Set tên, thường dùng IN sẽ chính xác hơn LIKE từng cái
            return root.join("studentClasses")
                    .join("educationProgram")
                    .get("name").in(epNames);
        };
    }

    // 7. Lọc theo Bộ môn (Join 3 tầng: Student -> Class -> EP -> SubDept)
    public static Specification<Student> hasSubDepartmentId(Set<String> subDeptIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(subDeptIds)) return null;
            query.distinct(true);
            return root.join("studentClasses")
                    .join("educationProgram")
                    .join("subDepartment")
                    .get("id").in(subDeptIds);
        };
    }

    // 8. Lọc theo Khoa (Join 4 tầng: Student -> Class -> EP -> SubDept -> Dept)
    public static Specification<Student> hasDepartmentId(Set<String> deptIds) {
        return (root, query, cb) -> {
            if (CollectionUtils.isEmpty(deptIds)) return null;
            query.distinct(true);
            return root.join("studentClasses")
                    .join("educationProgram")
                    .join("subDepartment")
                    .join("department")
                    .get("id").in(deptIds);
        };
    }
}