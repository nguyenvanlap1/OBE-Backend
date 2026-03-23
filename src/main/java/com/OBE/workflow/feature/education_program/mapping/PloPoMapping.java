package com.OBE.workflow.feature.education_program.mapping;

import com.OBE.workflow.feature.education_program.plo.PLO;
import com.OBE.workflow.feature.education_program.po.PO;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(
        name = "plo_po_mapping",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"ma_po", "ma_plo"}  // đảm bảo mỗi cặp PO–PLO chỉ có 1 row
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PloPoMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ ID tự gen

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_plo", nullable = false)
    private PLO plo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_po", nullable = false)
    private PO po;

    @Column(name = "weight")
    private Double weight;
}