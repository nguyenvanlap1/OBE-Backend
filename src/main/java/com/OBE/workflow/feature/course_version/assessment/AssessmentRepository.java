package com.OBE.workflow.feature.course_version.assessment;

import com.OBE.workflow.feature.course_version.CourseVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    Optional<Assessment> findByAssessmentCodeAndCourseVersion(String assessmentCode, CourseVersion version);
}
