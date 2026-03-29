package com.OBE.workflow.feature.education_program.program_course_detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramCourseDetailRepository extends JpaRepository<ProgramCourseDetail, Long> {
    // Lấy tất cả học phần thuộc một chương trình đào tạo
    List<ProgramCourseDetail> findByEducationProgramId(String educationProgramId);
    // Lọc học phần theo chương trình và khối kiến thức
    List<ProgramCourseDetail> findByEducationProgramIdAndKnowledgeBlockId(String programId, String kbId);
    // Xóa tất cả chi tiết của một chương trình (Dùng khi update toàn bộ danh sách)
    @Modifying
    @Query("DELETE FROM ProgramCourseDetail p WHERE p.educationProgram.id = :programId")
    void deleteByEducationProgramId(String programId);

    boolean existsByEducationProgramIdAndCourseVersionCourseId(String programId, String courseId);

    void deleteByEducationProgramIdAndCourseVersionCourseId(String programId, String courseId);

    Optional<ProgramCourseDetail> findByEducationProgramIdAndCourseVersionCourseId(String programId, String courseId);
}
