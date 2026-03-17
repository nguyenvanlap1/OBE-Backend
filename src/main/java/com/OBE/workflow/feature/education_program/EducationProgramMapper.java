package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EducationProgramMapper {

    // --- Entity sang Response ---
    @Mapping(target = "subDepartmentId", source = "subDepartment.id")
    @Mapping(target = "subDepartmentName", source = "subDepartment.name")
    @Mapping(target = "departmentId", source = "subDepartment.department.id")
    @Mapping(target = "departmentName", source = "subDepartment.department.name")
    @Mapping(target = "schoolYearIds", expression = "java(entity.getSchoolYears() != null ? entity.getSchoolYears().stream().map(sy -> sy.getId()).toList() : java.util.Collections.emptyList())")
    @Mapping(target = "totalCourses", expression = "java(entity.getCourseVersions() != null ? entity.getCourseVersions().size() : 0)")
    EducationProgramResponse toResponse(EducationProgram entity);

    // --- Request sang Entity (Tạo mới) ---
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "schoolYears", ignore = true)
    @Mapping(target = "courseVersions", ignore = true)
    EducationProgram toEntity(EducationProgramRequest request);

    // --- Request sang Entity (Cập nhật bản ghi có sẵn) ---
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật ID (Primary Key)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "schoolYears", ignore = true)
    @Mapping(target = "courseVersions", ignore = true)
    void updateEntity(EducationProgramRequest request, @MappingTarget EducationProgram entity);
}