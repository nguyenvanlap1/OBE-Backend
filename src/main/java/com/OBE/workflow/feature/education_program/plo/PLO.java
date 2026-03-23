package com.OBE.workflow.feature.education_program.plo;

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
        name = "chuan_dau_ra_chuong_trinh",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_plo_code_program",
                        columnNames = {"ma_plo", "ma_chuong_trinh"}
                )
        },
        indexes = {
                @Index(name = "idx_plo_program", columnList = "ma_chuong_trinh")
        }
)
public class PLO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_plo", nullable = false)
    private String ploCode;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chuong_trinh", nullable = false)
    @JsonIgnore
    private EducationProgram educationProgram;

    @OneToMany(
            mappedBy = "plo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PloPoMapping> mappings = new ArrayList<>();
}