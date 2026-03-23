package com.OBE.workflow.feature.course_version.mapping;


import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.co.CO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoCloMappingRepository extends JpaRepository<CoCloMapping, Long> {

    // Truy vấn tất cả mapping CO-CLO thuộc về một Version học phần
    @Query("SELECT m FROM CoCloMapping m WHERE m.clo.courseVersion = :version")
    List<CoCloMapping> findByCourseVersion(@Param("version") CourseVersion version);

    @Modifying
    @Query("""
    DELETE FROM CoCloMapping m
    WHERE m.clo.courseVersion = :version
    """)
    void deleteByCourseVersion(@Param("version") CourseVersion version);
}
