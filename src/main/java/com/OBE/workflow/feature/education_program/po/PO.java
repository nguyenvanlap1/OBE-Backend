package com.OBE.workflow.feature.education_program.po;

import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.mapping.PloPoMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "muc_tieu_dao_tao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_po_code_program",
                        columnNames = {"ma_po", "ma_chuong_trinh"}
                )
        },
        indexes = {
        @Index(name = "idx_po_program", columnList = "ma_chuong_trinh")
}
)
public class PO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_po", nullable = false)
    private String poCode;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chuong_trinh", nullable = false)
    @JsonIgnore
    private EducationProgram educationProgram;

    @OneToMany(
            mappedBy = "po",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PloPoMapping> mappings = new ArrayList<>();
}