package com.OBE.workflow.feature.course_version.clo;

import com.OBE.workflow.feature.course_version.CourseVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CLORepository extends JpaRepository<CLO, Long> {
    Optional<CLO> findByCloCodeAndCourseVersion(String cloCode, CourseVersion version);
}
