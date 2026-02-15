package com.OBE.workflow.mapper;

import com.OBE.workflow.dto.response.DepartmentResponse;
import com.OBE.workflow.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    // MapStruct tự động hiểu: id -> id, name -> name, description -> description
    DepartmentResponse toResponse(Department department);
}