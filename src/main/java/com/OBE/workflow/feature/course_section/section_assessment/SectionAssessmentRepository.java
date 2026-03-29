package com.OBE.workflow.feature.course_section.section_assessment;

import com.OBE.workflow.feature.course_section.section_assessment.SectionAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionAssessmentRepository extends JpaRepository<SectionAssessment, Long> {

    // Tìm theo mã số Long của lớp và mã số Long của bài đánh giá
    Optional<SectionAssessment> findByCourseSectionIdAndSectionAssessmentCode(
            String courseSectionId,
            Long sectionAssessmentCode
    );

    List<SectionAssessment> findByCourseSectionId(String courseSectionId);
}