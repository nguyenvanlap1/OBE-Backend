package com.OBE.workflow.feature.course_version.assessment;

import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.mapping.AssessmentCloMapping;
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
        name = "diem_thanh_phan",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_assessment_code_version",
                        columnNames = {"ma_danh_gia", "ma_hoc_phan", "so_thu_tu_phien_ban"}
                )
        }
)
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_danh_gia", nullable = false)
    private String assessmentCode;

    @Column(name = "ten_thanh_phan", nullable = false)
    private String name; // Ví dụ: Thi lý thuyết cuối kỳ, Bài tập...

    @Column(name = "quy_dinh", nullable = false)
    private String regulation; // Ví dụ: Bắt buộc

    @Column(name = "trong_so", nullable = false)
    private Double weight; // Ví dụ: 0.5 (tương đương 50%)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan", nullable = false),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban", nullable = false)
    })
    private CourseVersion courseVersion;

    @Builder.Default
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentCloMapping> assessmentCloMappings = new ArrayList<>();
}
