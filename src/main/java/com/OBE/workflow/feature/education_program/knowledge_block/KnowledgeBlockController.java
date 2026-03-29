package com.OBE.workflow.feature.education_program.knowledge_block;

import com.OBE.workflow.conmon.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-blocks")
@RequiredArgsConstructor
@CrossOrigin("*")
public class KnowledgeBlockController {
    private final KnowledgeBlockService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<KnowledgeBlock>>> getAll() {
        List<KnowledgeBlock> data = service.getAll();
        return ResponseEntity.ok(
                ApiResponse.<List<KnowledgeBlock>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách khối kiến thức thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KnowledgeBlock>> getById(@PathVariable("id") String id) {
        KnowledgeBlock data = service.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<KnowledgeBlock>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy chi tiết khối kiến thức thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KnowledgeBlock>> create(@RequestBody KnowledgeBlock kb) {
        KnowledgeBlock created = service.save(kb);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<KnowledgeBlock>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tạo khối kiến thức thành công")
                        .data(created)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KnowledgeBlock>> update(@PathVariable("id") String id, @RequestBody KnowledgeBlock kb) {
        kb.setId(id);
        KnowledgeBlock updated = service.save(kb);
        return ResponseEntity.ok(
                ApiResponse.<KnowledgeBlock>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật khối kiến thức thành công")
                        .data(updated)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") String id) {
        service.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Xóa khối kiến thức thành công")
                        .build()
        );
    }
}