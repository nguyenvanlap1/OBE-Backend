package com.OBE.workflow.feature.department;

import com.OBE.workflow.feature.department.request.DepartmentRequest;
import com.OBE.workflow.feature.department.response.DepartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    // MapStruct tự động hiểu: id -> id, name -> name, description -> description
    DepartmentResponse toResponse(Department department);
    // Thêm dòng này:
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật ID
    void updateDepartment(@MappingTarget Department department, DepartmentRequest request);
}