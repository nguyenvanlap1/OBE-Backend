package com.OBE.workflow.feature.officer.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficerResponse {
    private String id;
    private String fullName;
    private String gender;
}