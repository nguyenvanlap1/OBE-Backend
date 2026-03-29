package com.OBE.workflow.permission.entity.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRoleId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String permission;
    private String role;
}
