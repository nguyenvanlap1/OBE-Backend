package com.OBE.workflow.feature.officer;

import com.OBE.workflow.feature.officer.request.OfficerRequest;
import com.OBE.workflow.feature.officer.response.OfficerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OfficerMapper {

    OfficerResponse toResponse(Officer officer);

    Officer toEntity(OfficerRequest request);

    void updateOfficer(@MappingTarget Officer officer, OfficerRequest request);
}