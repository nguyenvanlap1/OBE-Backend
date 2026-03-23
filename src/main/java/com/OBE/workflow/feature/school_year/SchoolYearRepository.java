package com.OBE.workflow.feature.school_year;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolYearRepository extends JpaRepository<SchoolYear, String>, JpaSpecificationExecutor<SchoolYear> {
    // Chỉ cần CRUD cơ bản để Admin thêm/xóa/sửa các Khóa (K44, K45...)
}