package com.OBE.workflow.feature.course_version.mapping;


import com.OBE.workflow.feature.course_version.assessment.Assessment;
import com.OBE.workflow.feature.course_version.clo.CLO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "assessment_clo_mapping",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_assessment_clo",
                columnNames = {"assessment_id", "clo_id"} // Đảm bảo 1 cặp Assessment-CLO chỉ xuất hiện 1 lần
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentCloMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ Chuyển sang ID tự gen để đồng bộ cấu trúc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clo_id", nullable = false)
    private CLO clo;

    @Column(name = "weight", nullable = false)
    private Double weight;
}