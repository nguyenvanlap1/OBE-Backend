package com.OBE.workflow.feature.student_class;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, String>, JpaSpecificationExecutor<StudentClass> {

    // Tối ưu hiệu năng: Join Fetch để tránh N+1 khi lấy dữ liệu cho Response
    @Query("SELECT sc FROM StudentClass sc " +
            "JOIN FETCH sc.schoolYear " +
            "JOIN FETCH sc.educationProgram ep " +
            "JOIN FETCH ep.subDepartment sd " +
            "JOIN FETCH sd.department")
    List<StudentClass> findAllWithDepth();

    boolean existsBySchoolYearId(String id);
}