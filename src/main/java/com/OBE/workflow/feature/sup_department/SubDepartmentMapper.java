package com.OBE.workflow.feature.sup_department;

import com.OBE.workflow.feature.sup_department.request.SubDepartmentRequest;
import com.OBE.workflow.feature.sup_department.response.SubDepartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubDepartmentMapper {

    // Ánh xạ từ Entity sang Response (lấy ID của khoa để trả về)
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    SubDepartmentResponse toResponse(SubDepartment subDepartment);

    // Ánh xạ từ Request sang Entity (bỏ qua field department vì sẽ set thủ công trong Service)
    @Mapping(target = "department", ignore = true)
    SubDepartment toEntity(SubDepartmentRequest request);

    // Cập nhật Entity từ Request (giống như bài trước chúng ta làm)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateSubDepartment(@MappingTarget SubDepartment subDepartment, SubDepartmentRequest request);
}