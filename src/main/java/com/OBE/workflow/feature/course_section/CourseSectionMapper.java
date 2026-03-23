package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponse;
import com.OBE.workflow.feature.course_section.request.CourseSectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseSectionMapper {

    @Mapping(target = "courseId", source = "courseVersion.course.id")
    @Mapping(target = "courseName", source = "courseVersion.name")
    @Mapping(target = "versionNumber", source = "courseVersion.versionNumber")
    @Mapping(target = "lecturerId", source = "lecturer.id")
    @Mapping(target = "lecturerName", source = "lecturer.fullName")
    CourseSectionResponse toResponse(CourseSection entity);

    // 2. Chuyển từ Request sang Entity
    // Bỏ qua (ignore) các Entity quan hệ để gán thủ công trong Service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "courseVersion", ignore = true) // Bỏ qua theo ý bạn
    @Mapping(target = "lecturer", ignore = true)      // Bỏ qua theo ý bạn
    CourseSection toEntity(CourseSectionRequest request);
}
