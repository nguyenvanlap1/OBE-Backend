package com.OBE.workflow.feature.course_section.grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // 1. Tìm điểm cụ thể của một sinh viên cho một cột điểm (VD: Giữa kỳ của SV A)
    Optional<Grade> findByEnrollmentIdAndAssessmentCode(Long enrollmentId, String assessmentCode);

    // 2. Lấy tất cả các cột điểm của một sinh viên trong một lớp
    List<Grade> findByEnrollmentId(Long enrollmentId);

    // 3. Kiểm tra xem sinh viên đã có điểm cho cột này chưa
    boolean existsByEnrollmentIdAndAssessmentCode(Long enrollmentId, String assessmentCode);

    // 4. Lấy danh sách điểm theo mã đánh giá của cả một lớp (Hữu ích để thống kê phổ điểm)
    @Query("SELECT g FROM Grade g WHERE g.enrollment.courseSection.id = :sectionId AND g.assessmentCode = :code")
    List<Grade> findAllBySectionAndAssessment(@Param("sectionId") String sectionId, @Param("code") String code);

    // 5. Xóa một cột điểm cụ thể
    void deleteByEnrollmentIdAndAssessmentCode(Long enrollmentId, String assessmentCode);
}
