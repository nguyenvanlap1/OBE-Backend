package com.OBE.workflow.feature.education_program.mapping.dto;

import com.OBE.workflow.feature.education_program.mapping.PloCoMapping;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PloCoMappingResponse {
    private String ploCode;
    private String coCode;
    private Double weight;

    public static PloCoMappingResponse fromEntity(PloCoMapping entity) {
        if (entity == null) return null;

        return PloCoMappingResponse.builder()
                .ploCode(entity.getPlo() != null ? entity.getPlo().getPloCode() : null)
                .coCode(entity.getCo() != null ? entity.getCo().getCoCode() : null)
                .weight(entity.getWeight())
                .build();
    }
}