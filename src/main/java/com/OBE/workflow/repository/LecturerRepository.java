package com.OBE.workflow.repository;

import com.OBE.workflow.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {
    // Tìm kiếm giảng viên theo họ tên (phục vụ chức năng tìm kiếm trên UI)
    List<Lecturer> findByFullNameContainingIgnoreCase(String fullName);
}
