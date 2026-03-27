package com.OBE.workflow.feature.course_version.co;

import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.CourseVersionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CORepository extends JpaRepository<CO, Long> {
    Optional<CO> findByCoCodeAndCourseVersion(String coCode, CourseVersion version);
//    boolean existsByCoCodeAndCourseVersionId(String coCode, CourseVersionId id);
}
