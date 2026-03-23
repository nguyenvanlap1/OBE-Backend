package com.OBE.workflow.feature.course_section;

import org.springframework.data.jpa.domain.Specification;

public class CourseSectionSpecification {

    public static Specification<CourseSection> hasSectionCode(String sectionCode) {
        return (root, query, cb) -> sectionCode == null ? null :
                cb.like(cb.lower(root.get("sectionCode")), "%" + sectionCode.toLowerCase() + "%");
    }

    public static Specification<CourseSection> hasSemester(Integer semester) {
        return (root, query, cb) -> semester == null ? null :
                cb.equal(root.get("semester"), semester);
    }

    public static Specification<CourseSection> hasAcademicYear(String year) {
        return (root, query, cb) -> year == null ? null :
                cb.equal(root.get("academicYear"), year);
    }

    // Lọc theo giảng viên (truy vấn lồng qua thuộc tính id của Lecturer)
    public static Specification<CourseSection> hasLecturerId(String lecturerId) {
        return (root, query, cb) -> lecturerId == null ? null :
                cb.equal(root.get("lecturer").get("id"), lecturerId);
    }
}