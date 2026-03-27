package com.OBE.workflow.feature.course_section;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CourseSectionRepository extends JpaRepository<CourseSection, String>, JpaSpecificationExecutor<CourseSection> {
}