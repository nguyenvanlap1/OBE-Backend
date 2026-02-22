package com.OBE.workflow.feature.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {
    // JpaRepository đã cung cấp sẵn các hàm findById, save, deleteById...
    // Bạn có thể tìm nhanh theo tên khoa nếu cần
    Optional<Department> findByName(String name);
    boolean existsByName(String name);
}