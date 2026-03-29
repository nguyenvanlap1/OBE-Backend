package com.OBE.workflow.feature.education_program.mapping;

import camundajar.impl.scala.None;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PloCoMappingRepository extends JpaRepository<PloCoMapping, Long> {

    @Query("SELECT m FROM PloCoMapping m " +
            "JOIN FETCH m.plo " +
            "JOIN FETCH m.co " +
            "WHERE m.programCourseDetail.id = :detailId")
    List<PloCoMapping> findByProgramCourseDetailId(@Param("detailId") Long detailId);
    // Tìm mapping dựa trên ID các thực thể
    Optional<PloCoMapping> findByPloIdAndCoIdAndProgramCourseDetailId(Long ploId, Long coId, Long detailId);

    // Tìm mapping dựa trên mã Code (Dùng cho hàm Remove)
    @Query("SELECT m FROM PloCoMapping m " +
            "WHERE m.programCourseDetail.educationProgram.id = :programId " +
            "AND m.programCourseDetail.courseVersion.course.id = :courseId " +
            "AND m.plo.ploCode = :ploCode " +
            "AND m.co.coCode = :coCode")
    Optional<PloCoMapping> findMappingByCodes(String programId, String courseId, String ploCode, String coCode);}