package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.feature.course_section.request.CourseSectionFilterRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

public class CourseSectionSpecification {

    public static Specification<CourseSection> filterBy(CourseSectionFilterRequest filter) {
        return Specification.where(hasId(filter.getId()))
                .and(hasSemester(filter.getSemesterTerm(), filter.getSemesterAcademicYear()))
                .and(hasCourseId(filter.getCourseId()))
                .and(hasCourseName(filter.getCourseVersionName()))
                .and(hasLecturerName(filter.getLecturerName()))
                .and(hasSubDepartmentId(filter.getSubDepartmentId()));
    }

    public static Specification<CourseSection> hasId(String id) {
        return (root, query, cb) -> (id == null || id.isEmpty()) ? null :
                cb.like(cb.lower(root.get("sectionCode")), "%" + id.toLowerCase() + "%");
    }

    public static Specification<CourseSection> hasSemester(Integer term, String year) {
        return (root, query, cb) -> {
            if (term == null && (year == null || year.isEmpty())) return null;
            Join<Object, Object> semesterJoin = root.join("semester");
            if (term != null && year != null) {
                return cb.and(
                        cb.equal(semesterJoin.get("term"), term),
                        cb.equal(semesterJoin.get("academicYear"), year)
                );
            }
            return term != null ? cb.equal(semesterJoin.get("term"), term)
                    : cb.equal(semesterJoin.get("academicYear"), year);
        };
    }

    public static Specification<CourseSection> hasCourseId(String courseId) {
        return (root, query, cb) -> {
            if (courseId == null || courseId.isEmpty()) return null;
            // CourseSection -> CourseVersion -> Course
            return cb.equal(root.join("courseVersion").join("course").get("id"), courseId);
        };
    }

    public static Specification<CourseSection> hasCourseName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty()) ? null :
                cb.like(cb.lower(root.join("courseVersion").join("course").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<CourseSection> hasLecturerName(String lecturerName) {
        return (root, query, cb) -> (lecturerName == null || lecturerName.isEmpty()) ? null :
                cb.like(cb.lower(root.join("lecturer").get("fullName")), "%" + lecturerName.toLowerCase() + "%");
    }

    public static Specification<CourseSection> hasSubDepartmentId(String subDeptId) {
        return (root, query, cb) -> (subDeptId == null || subDeptId.isEmpty()) ? null :
                cb.equal(root.join("courseVersion").join("course").join("subDepartment").get("id"), subDeptId);
    }
}
