package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.feature.education_program.mapping.PloPoMapping;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequestUpdateDetail;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponseDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EducationProgramMapper {

    // --- Entity sang Response ---
    @Mapping(target = "subDepartmentId", source = "subDepartment.id")
    @Mapping(target = "subDepartmentName", source = "subDepartment.name")
    @Mapping(target = "departmentId", source = "subDepartment.department.id")
    @Mapping(target = "departmentName", source = "subDepartment.department.name")
    @Mapping(target = "schoolYearIds", expression = "java(entity.getSchoolYears() != null ? entity.getSchoolYears().stream().map(sy -> sy.getId()).toList() : java.util.Collections.emptyList())")
    @Mapping(target = "totalCourses", expression = "java(entity.getCourseDetails() != null ? entity.getCourseDetails().size() : 0)")
    EducationProgramResponse toResponse(EducationProgram entity);

    // --- Request sang Entity (Tạo mới) ---
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "schoolYears", ignore = true)
    EducationProgram toEntity(EducationProgramRequest request);

    // --- Request sang Entity (Cập nhật bản ghi có sẵn) ---
    @Mapping(target = "id", ignore = true) // Không cho phép cập nhật ID (Primary Key)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "schoolYears", ignore = true)
    void updateEntity(EducationProgramRequest request, @MappingTarget EducationProgram entity);

    // --- Entity sang ResponseDetail (Đầy đủ PO, PLO, Mapping) ---
    @Mapping(target = "subDepartmentId", source = "entity.subDepartment.id")
    @Mapping(target = "subDepartmentName", source = "entity.subDepartment.name")
    // Sử dụng helper methods bên dưới để map danh sách
    @Mapping(target = "pos", expression = "java(mapPos(entity))")
    @Mapping(target = "plos", expression = "java(mapPlos(entity))")
    @Mapping(target = "ploPoMappings", expression = "java(mapMappings(ploPoMappings))")
    @Mapping(target = "schoolYearIds",  ignore = true)
    EducationProgramResponseDetail toDetailResponse(EducationProgram entity, List<PloPoMapping> ploPoMappings);

    @Mapping(target = "subDepartment", ignore = true) // Xử lý thủ công trong Service
    @Mapping(target = "schoolYears", ignore = true)   // Xử lý thủ công qua Repository.findAllById()
    @Mapping(target = "pos", ignore = true)            // Xử lý logic con (PO)
    @Mapping(target = "plos", ignore = true)           // Xử lý logic con (PLO)
    EducationProgram toEntity(EducationProgramRequestUpdateDetail requestCreateDetail);
    // --- Helper Methods (Dùng để bóc tách dữ liệu sang DTO) ---

    default List<EducationProgramResponseDetail.PoResponse> mapPos(EducationProgram entity) {
        // Bạn cần thêm field List<PO> pos vào Entity EducationProgram (có mappedBy)
        return entity.getPos() == null ? java.util.Collections.emptyList() :
                entity.getPos().stream().map(po -> EducationProgramResponseDetail.PoResponse.builder()
                        .id(po.getId())
                        .poCode(po.getPoCode())
                        .content(po.getContent())
                        .build()).toList();
    }

    default List<EducationProgramResponseDetail.PloResponse> mapPlos(EducationProgram entity) {
        return entity.getPlos() == null ? java.util.Collections.emptyList() :
                entity.getPlos().stream().map(plo -> EducationProgramResponseDetail.PloResponse.builder()
                        .id(plo.getId())
                        .ploCode(plo.getPloCode())
                        .content(plo.getContent())
                        .build()).toList();
    }

    default List<EducationProgramResponseDetail.PloPoMappingResponse> mapMappings(List<PloPoMapping> mappings) {
        return mappings == null ? java.util.Collections.emptyList() :
                mappings.stream().map(m -> EducationProgramResponseDetail.PloPoMappingResponse.builder()
                        .ploId(m.getPlo().getId())
                        .ploCode(m.getPlo().getPloCode())
                        .poId(m.getPo().getId())
                        .poCode(m.getPo().getPoCode())
                        .weight(m.getWeight())
                        .build()).toList();
    }

    // ================= REQUEST → ENTITY =================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "pos", ignore = true)
    @Mapping(target = "plos", ignore = true)
    @Mapping(target = "schoolYears", ignore = true)
    void updateEntity(EducationProgramRequestUpdateDetail request,
                      @MappingTarget EducationProgram entity);
}