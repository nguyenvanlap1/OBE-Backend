package com.OBE.workflow.feature.education_program.knowledge_block;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khoi_kien_thuc")
public class KnowledgeBlock {
    @Id
    @Column(name = "ma_khoi_kien_thuc", nullable = false)
    private String id; // Ví dụ: CSN, CN...

    @Column(name = "ten_khoi_kien_thuc", unique = true, nullable = false)
    private String name; // Ví dụ: Cơ sở ngành...
}