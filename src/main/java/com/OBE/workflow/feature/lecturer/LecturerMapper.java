package com.OBE.workflow.feature.lecturer;

import com.OBE.workflow.feature.lecturer.request.LecturerRequest;
import com.OBE.workflow.feature.lecturer.response.LecturerResponse;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    // Ánh xạ từ Entity sang Response
    @Mapping(target = "subDepartmentIds", source = "subDepartments", qualifiedByName = "mapSubDepartmentsToIds")
    LecturerResponse toResponse(Lecturer lecturer);

    // Ánh xạ từ Request sang Entity
    // subDepartments sẽ được set thủ công trong Service bằng cách tìm theo IDs
    @Mapping(target = "subDepartments", ignore = true)
    Lecturer toEntity(LecturerRequest request);

    // Cập nhật Entity từ Request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subDepartments", ignore = true)
    void updateLecturer(@MappingTarget Lecturer lecturer, LecturerRequest request);

    // Logic phụ để chuyển đổi Set<SubDepartment> thành Set<String> IDs
    @Named("mapSubDepartmentsToIds")
    default Set<String> mapSubDepartmentsToIds(Set<SubDepartment> subDepartments) {
        if (subDepartments == null) return null;
        return subDepartments.stream()
                .map(SubDepartment::getId)
                .collect(Collectors.toSet());
    }
}