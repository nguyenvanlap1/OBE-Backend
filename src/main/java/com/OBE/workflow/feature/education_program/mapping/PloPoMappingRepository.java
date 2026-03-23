package com.OBE.workflow.feature.education_program.mapping;

import com.OBE.workflow.feature.education_program.EducationProgram;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PloPoMappingRepository extends JpaRepository<PloPoMapping, Long> {

    // Cách viết này cô lập hoàn toàn bảng Mapping, chỉ tập trung vào bảng PLO đã JOIN
    @Query("SELECT m FROM PloPoMapping m JOIN m.plo p WHERE p.educationProgram = :program")
    List<PloPoMapping> findAllByEducationProgram(@Param("program") EducationProgram program);

    @Modifying
    @Transactional
    @Query("DELETE FROM PloPoMapping m WHERE m.plo.id = :ploId")
    void deleteByPloId(Long ploId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PloPoMapping m WHERE m.po.id = :poId")
    void deleteByPoId(Long poId);

    @Modifying
    @Query("DELETE FROM PloPoMapping m WHERE m.plo.educationProgram.id = :programId")
    void deleteByEducationProgramId(@Param("programId") String programId);
}