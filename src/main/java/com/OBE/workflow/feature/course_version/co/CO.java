package com.OBE.workflow.feature.course_version.co;

import com.OBE.workflow.feature.course_version.mapping.CoCloMapping;
import com.OBE.workflow.feature.course_version.CourseVersion;
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
        name = "co",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_co_code_version",
                        columnNames = {"ma_co", "ma_hoc_phan", "so_thu_tu_phien_ban"}
                )
        }
)
public class CO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_co", nullable = false)
    private String coCode; // Ví dụ: CO1, CO2

    @Column(name = "noi_dung_muc_tieu", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan", nullable = false),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban", nullable = false)
    })
    private CourseVersion courseVersion;

    @Builder.Default
    @OneToMany(mappedBy = "co", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoCloMapping> coCloMappings = new ArrayList<>();
}
