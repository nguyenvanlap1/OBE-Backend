package com.OBE.workflow.feature.course_section.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

    // Tìm xem sinh viên đã đăng ký lớp này chưa (tránh đăng ký trùng)
    boolean existsByStudentIdAndCourseSectionId(String studentId, String courseSectionId);

    // Lấy danh sách đăng ký của một lớp học phần
    List<Enrollment> findByCourseSectionId(String courseSectionId);

    // Lấy tất cả các lớp mà một sinh viên đang theo học
    List<Enrollment> findByStudentId(String studentId);

    // Tìm kiếm chính xác một bản ghi đăng ký
    Optional<Enrollment> findByStudentIdAndCourseSectionId(String studentId, String courseSectionId);

    // Xóa đăng ký (khi sinh viên hủy lớp)
    void deleteByStudentIdAndCourseSectionId(String studentId, String courseSectionId);

    List<Enrollment> findAllByCourseSectionId(String sectionId);
}