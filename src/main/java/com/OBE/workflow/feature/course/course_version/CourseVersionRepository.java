package com.OBE.workflow.feature.course.course_version;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseVersionRepository
        extends JpaRepository<CourseVersion, CourseVersionId>,
        JpaSpecificationExecutor<CourseVersion> {

    // Sửa cái dòng lỗi trong ảnh của bạn thành:
    Optional<CourseVersion> findByCourseIdAndVersionNumber(String courseId, Integer versionNumber);

    // Và thêm hàm lấy Max Version để phục vụ cho Service:
    @Query("SELECT COALESCE(MAX(v.versionNumber), 0) FROM CourseVersion v WHERE v.course.id = :courseId")
    Integer findMaxVersionByCourseId(@Param("courseId") String courseId);

    long countByCourseId(String courseId);

    boolean existsByCourseId(String courseId);

    List<CourseVersion> findByCourseIdOrderByVersionNumberDesc(String courseId);
    // 1. Lấy phiên bản đang hoạt động (active) mới nhất
    @Query("SELECT v FROM CourseVersion v " +
            "WHERE v.course.id = :courseId " +
            "AND v.fromDate <= CURRENT_DATE " +
            "AND (v.toDate IS NULL OR v.toDate >= CURRENT_DATE) " +
            "ORDER BY v.versionNumber DESC LIMIT 1")
    Optional<CourseVersion> findActiveVersion(@Param("courseId") String courseId);

    // 2. Lấy phiên bản mới nhất bất kể trạng thái (Dùng Optional thay cho ScopedValue để chuẩn JPA)
    Optional<CourseVersion> findFirstByCourseIdOrderByVersionNumberDesc(String courseId);
}