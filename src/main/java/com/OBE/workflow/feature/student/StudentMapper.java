package com.OBE.workflow.feature.student;

import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.student.request.StudentRequest;
import com.OBE.workflow.feature.student.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    // Ánh xạ từ Entity sang Response: Chuyển đổi Set<EducationProgram> thành Set<String> IDs
    @Mapping(target = "educationProgramIds", source = "educationPrograms", qualifiedByName = "mapProgramsToIds")
    StudentResponse toResponse(Student student);

    // Ánh xạ từ Request sang Entity: Bỏ qua tập hợp Programs để xử lý thủ công trong Service
    @Mapping(target = "educationPrograms", ignore = true)
    Student toEntity(StudentRequest request);

    // Cập nhật Entity từ Request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "educationPrograms", ignore = true)
    void updateStudent(@MappingTarget Student student, StudentRequest request);

    // Logic phụ: Chuyển đổi Set thực thể sang Set mã ID (String)
    @Named("mapProgramsToIds")
    default Set<String> mapProgramsToIds(Set<EducationProgram> educationPrograms) {
        if (educationPrograms == null) return null;
        return educationPrograms.stream()
                .map(EducationProgram::getId)
                .collect(Collectors.toSet());
    }
}