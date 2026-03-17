package com.OBE.workflow.feature.course.course_version;

import com.OBE.workflow.feature.course.Course;
import com.OBE.workflow.feature.course.request.CourseCreateRequest;
import com.OBE.workflow.feature.course.request.CourseUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseVersionMapper {

    // Tạo version đầu tiên (v1)
    @Mapping(target = "versionNumber", constant = "1")
    @Mapping(target = "name", source = "request.defaultName")
    CourseVersion toInitialVersion(CourseCreateRequest request, Course course);


    // Tạo bản mới (v2, v3...) từ update request
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    CourseVersion toNewVersion(CourseUpdateRequest request);

    // Cập nhật đè lên bản hiện tại
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    void updateVersion(CourseUpdateRequest request, @MappingTarget CourseVersion version);
}