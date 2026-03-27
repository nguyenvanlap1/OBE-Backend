package com.OBE.workflow.feature.course_version.clo;

import com.OBE.workflow.feature.course_version.mapping.AssessmentCloMapping;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.mapping.CoCloMapping;
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
        name = "clo",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_clo_code_version",
                        columnNames = {"ma_clo", "ma_hoc_phan", "so_thu_tu_phien_ban"}
                )
        }
)
public class CLO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_clo", nullable = false)
    private String cloCode;

    @Column(name = "noi_dung_chuan_dau_ra", columnDefinition = "TEXT", nullable = false)
    private String content;

    // 1. Liên kết tới CourseVersion (Cha)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan", nullable = false),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban", nullable = false)
    })
    private CourseVersion courseVersion;

    @Builder.Default
    @OneToMany(mappedBy = "clo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoCloMapping> coCloMappings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "clo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentCloMapping> assessmentCloMappings = new ArrayList<>();
}