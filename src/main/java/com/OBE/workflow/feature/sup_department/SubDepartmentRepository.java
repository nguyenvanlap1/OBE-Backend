package com.OBE.workflow.feature.sup_department;

import com.OBE.workflow.feature.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubDepartmentRepository extends JpaRepository<SubDepartment, String>, JpaSpecificationExecutor<SubDepartment> {
    boolean existsByDepartment(Department department);
}