package com.OBE.workflow.feature.course_version.mapping;

import com.OBE.workflow.feature.course_version.clo.CLO;
import com.OBE.workflow.feature.course_version.co.CO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "co_clo_mapping",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_co_clo",
                columnNames = {"co_id", "clo_id"} // Đảm bảo một cặp CO-CLO không bị lặp lại
        )
)
public class CoCloMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ ID tự gen cho đồng nhất

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_id", nullable = false)
    private CO co;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clo_id", nullable = false)
    private CLO clo;

    @Column(name = "weight", nullable = false)
    private Double weight;
}