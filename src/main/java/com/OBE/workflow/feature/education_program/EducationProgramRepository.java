package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.feature.sup_department.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationProgramRepository extends JpaRepository<EducationProgram, String>, JpaSpecificationExecutor<EducationProgram> {
    boolean existsBySubDepartment(SubDepartment subDepartment);

    boolean existsBySchoolYearsId(String id);
    // JpaSpecificationExecutor cho phép chúng ta sử dụng Specification để tìm kiếm nâng cao
}