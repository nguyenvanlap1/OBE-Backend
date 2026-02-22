package com.OBE.workflow.conmon.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// File: dto/response/ApiResponse.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ hiện những trường có dữ liệu
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data; // Dùng Generic để chứa bất kỳ loại dữ liệu nào (Token, UserInfo, List...)
}
