package com.OBE.workflow.feature.course.clo;

import com.OBE.workflow.feature.course.clo.CLO;
import com.OBE.workflow.feature.course.co.CO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "co_clo_mapping")
@IdClass(CoCloMappingId.class)
public class CoCloMapping {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_id")
    private CO co;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clo_id")
    private CLO clo;

    @Column(name = "weight")
    private Double weight;
}