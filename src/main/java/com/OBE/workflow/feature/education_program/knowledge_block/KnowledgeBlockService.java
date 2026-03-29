package com.OBE.workflow.feature.education_program.knowledge_block;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeBlockService {
    private final KnowledgeBlockRepository repository;

    public List<KnowledgeBlock> getAll() {
        return repository.findAll();
    }

    public KnowledgeBlock getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,"Không tìm thấy Khối kiến thức: " + id));
    }

    @Transactional
    public KnowledgeBlock save(KnowledgeBlock kb) {
        return repository.save(kb);
    }

    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }
}