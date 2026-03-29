package com.OBE.workflow.feature.education_program.mapping.dto;

import lombok.Data;

@Data
public class PloCoMappingRequest {
    private String ploCode;
    private String coCode;
    private Double weight;
}