package com.OBE.workflow.feature.course.assessment_component;

import com.OBE.workflow.feature.course.clo.CLO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assessment_clo_mapping")
@IdClass(AssessmentCloMappingId.class)
public class AssessmentCloMapping {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clo_id")
    private CLO clo;

    @Column(name = "weight")
    private Double weight;
}