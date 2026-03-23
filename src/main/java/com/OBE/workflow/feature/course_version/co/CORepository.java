package com.OBE.workflow.feature.course_version.co;

import com.OBE.workflow.feature.course_version.CourseVersionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CORepository extends JpaRepository<CO, Long> {
//    boolean existsByCoCodeAndCourseVersionId(String coCode, CourseVersionId id);
}
