package com.OBE.workflow.feature.supDepartment;

import com.OBE.workflow.feature.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubDepartmentRepository extends JpaRepository<SubDepartment, String>, JpaSpecificationExecutor<SubDepartment> {
    boolean existsByDepartment(Department department);
}