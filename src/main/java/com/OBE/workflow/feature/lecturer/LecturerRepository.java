package com.OBE.workflow.feature.lecturer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String>, JpaSpecificationExecutor<Lecturer> {
    // Repository này giờ có thể thực hiện tìm kiếm nâng cao với Specification
}