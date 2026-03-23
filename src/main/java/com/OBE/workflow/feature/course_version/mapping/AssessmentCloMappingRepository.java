package com.OBE.workflow.feature.course_version.mapping;

import com.OBE.workflow.feature.course_version.CourseVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentCloMappingRepository extends JpaRepository<AssessmentCloMapping, Long> {

    // Truy vấn tất cả mapping thuộc về một Version học phần
    @Query("SELECT m FROM AssessmentCloMapping m WHERE m.assessment.courseVersion = :version")
    List<AssessmentCloMapping> findByCourseVersion(@Param("version") CourseVersion version);

    // Xóa tất cả mapping của một bài đánh giá cụ thể
    void deleteByAssessmentId(Long assessmentId);

    @Modifying
    @Query("""
    DELETE FROM CoCloMapping m
    WHERE m.clo.courseVersion = :version
    """)
    void deleteByCourseVersion(@Param("version") CourseVersion version);
}
