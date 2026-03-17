package com.OBE.workflow.feature.course.clo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoCloMappingId implements Serializable {
    private Long co;
    private Long clo;
}
