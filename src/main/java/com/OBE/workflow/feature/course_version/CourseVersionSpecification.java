package com.OBE.workflow.feature.course_version;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class CourseVersionSpecification {

    public static Specification<CourseVersion> hasCourseId(String courseId) {
        return (root, query, cb) ->
                courseId == null ? null :
                        cb.like(cb.lower(root.get("course").get("id")),
                                "%" + courseId.toLowerCase() + "%");
    }

    public static Specification<CourseVersion> hasVersionNumber(Integer versionNumber) {
        return (root, query, cb) ->
                versionNumber == null ? null :
                        cb.like(root.get("versionNumber").as(String.class),
                                "%" + versionNumber + "%");
    }

    public static Specification<CourseVersion> hasCredits(Integer credits) {
        return (root, query, cb) ->
                credits == null ? null :
                        cb.equal(root.get("credits"), credits);
    }

    // Bổ sung filter theo tên gốc của Course
    public static Specification<CourseVersion> hasCourseName(String courseName) {
        return (root, query, cb) ->
                courseName == null ? null :
                        cb.like(
                                cb.lower(root.get("course").get("courseName")),
                                "%" + courseName.toLowerCase() + "%"
                        );
    }

    // Phiên bản đang hiệu lực (toDate IS NULL)
    public static Specification<CourseVersion> isActive() {
        return (root, query, cb) ->
                cb.isNull(root.get("toDate"));
    }

    // Bổ sung filter theo mã bộ môn (SubDepartment)
    public static Specification<CourseVersion> hasSubDepartmentId(String subDepartmentId) {
        return (root, query, cb) ->
                subDepartmentId == null ? null :
                        cb.equal(
                                root.get("course").get("subDepartment").get("id"),
                                subDepartmentId
                        );
    }

    public static Specification<CourseVersion> hasDepartmentId(String departmentId) {
        return (root, query, cb) -> {
            if (departmentId == null) return null;

            // Join từ CourseVersion -> Course -> SubDepartment -> Department
            return cb.equal(
                    root.join("course")
                            .join("subDepartment")
                            .join("department")
                            .get("id"),
                    departmentId
            );
        };
    }

    public static Specification<CourseVersion> hasEducationProgramId(String educationProgramId) {
        return (root, query, cb) -> {
            if (educationProgramId == null) return null;

            // Vì là @ManyToMany, ta phải JOIN sang bảng EducationProgram
            // "educationPrograms" là tên biến bạn vừa thêm vào CourseVersion
            return cb.equal(root.join("educationPrograms").get("id"), educationProgramId);
        };
    }

    public static Specification<CourseVersion> isLatestVersion() {
        return (root, query, cb) -> {
            // Thêm dòng này để IDE không báo NullPointerException
            if (query == null) return null;
            Subquery<Integer> maxVersionQuery = query.subquery(Integer.class);
            Root<CourseVersion> subRoot = maxVersionQuery.from(CourseVersion.class);

            maxVersionQuery.select(cb.max(subRoot.get("versionNumber")))
                    .where(cb.equal(subRoot.get("course"), root.get("course")));

            return cb.equal(root.get("versionNumber"), maxVersionQuery);
        };
    }
}