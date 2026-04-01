package com.OBE.workflow.authorization.permission.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class PermissionTreeResponse {
    private String id;
    private String name;
    private String description;
    private Set<String> allowedScopes; // Trả về String để Frontend dễ đọc
    private List<PermissionTreeResponse> children;
}