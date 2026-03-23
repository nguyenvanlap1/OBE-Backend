package com.OBE.workflow.feature.course_section;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long>, JpaSpecificationExecutor<CourseSection> {

    // Tìm lớp học phần theo mã (VD: CT101-01)
    Optional<CourseSection> findBySectionCode(String sectionCode);

    // Tìm tất cả các lớp của một giảng viên trong một học kỳ cụ thể
    List<CourseSection> findByLecturerIdAndSemesterAndAcademicYear(String lecturerId, Integer semester, String academicYear);

    // Kiểm tra sự tồn tại của mã lớp
    boolean existsBySectionCode(String sectionCode);
}