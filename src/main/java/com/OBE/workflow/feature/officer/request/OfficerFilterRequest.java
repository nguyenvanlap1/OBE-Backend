package com.OBE.workflow.feature.officer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OfficerFilterRequest {
    private String id;
    private String fullName;
    private String gender;
}
