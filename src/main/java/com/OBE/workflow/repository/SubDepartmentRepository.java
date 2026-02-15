package com.OBE.workflow.repository;

import com.OBE.workflow.entity.SubDepartment;
import com.OBE.workflow.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubDepartmentRepository extends JpaRepository<SubDepartment, String> {

    // Tìm tất cả bộ môn thuộc về một khoa cụ thể
    // Rất quan trọng để lọc dữ liệu trên UI React/Tailwind
    List<SubDepartment> findByDepartment(Department department);

    // Tìm bộ môn theo tên (Nếu muốn kiểm tra trùng lặp trong cùng một khoa)
    List<SubDepartment> findByName(String name);

    boolean existsByDepartment(Department department);
}