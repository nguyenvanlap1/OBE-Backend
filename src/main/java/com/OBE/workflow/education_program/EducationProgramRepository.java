package com.OBE.workflow.education_program;

import com.OBE.workflow.feature.supDepartment.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationProgramRepository extends JpaRepository<EducationProgram, String>, JpaSpecificationExecutor<EducationProgram> {
    boolean existsBySubDepartment(SubDepartment subDepartment);
    // JpaSpecificationExecutor cho phép chúng ta sử dụng Specification để tìm kiếm nâng cao
}