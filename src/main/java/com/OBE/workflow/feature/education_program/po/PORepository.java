package com.OBE.workflow.feature.education_program.po;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PORepository extends JpaRepository<PO, Long> {
    // Tìm tất cả PO thuộc một chương trình đào tạo cụ thể
    // 1. Tìm tất cả PO thuộc một chương trình đào tạo cụ thể
    List<PO> findByEducationProgramId(String educationProgramId);

    // 2. Kiểm tra tồn tại PO theo mã và chương trình
    boolean existsByPoCodeAndEducationProgramId(String poCode, String educationProgramId);

    // 3. Lấy PO theo poCode và chương trình (trả về Optional)
    Optional<PO> findByPoCodeAndEducationProgramId(String poCode, String educationProgramId);
}
