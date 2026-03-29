package com.OBE.workflow.feature.education_program.knowledge_block;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeBlockRepository extends JpaRepository<KnowledgeBlock, String> {
    // Bạn có thể thêm tìm kiếm theo tên nếu cần
    boolean existsByName(String name);
}