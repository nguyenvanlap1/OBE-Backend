package com.OBE.workflow.feature.semester;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long>, JpaSpecificationExecutor<Semester> {

    /**
     * Kiểm tra sự tồn tại của cặp Học kỳ và Năm học
     * Phục vụ logic validation trong Service
     */
    boolean existsByTermAndAcademicYear(Integer term, String academicYear);

    /**
     * Tìm học kỳ dựa trên cặp giá trị duy nhất
     */
    Optional<Semester> findByTermAndAcademicYear(Integer term, String academicYear);

    /**
     * Tìm học kỳ mới nhất dựa trên ID (giả định ID tăng dần theo thời gian)
     */
    Optional<Semester> findFirstByOrderByIdDesc();
}