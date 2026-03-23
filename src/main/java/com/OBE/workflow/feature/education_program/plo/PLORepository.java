package com.OBE.workflow.feature.education_program.plo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PLORepository extends JpaRepository<PLO, Long> {
    // Tìm tất cả PLO thuộc một chương trình đào tạo
    List<PLO> findByEducationProgramId(String educationProgramId);

    boolean existsByPloCodeAndEducationProgramId(String ploCode, String id);

    Optional<PLO> findByPloCodeAndEducationProgramId(String ploCode, String id);
}
