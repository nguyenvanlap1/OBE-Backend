package com.OBE.workflow.feature.course;

import com.OBE.workflow.feature.course.request.CourseCreateRequest;
import com.OBE.workflow.feature.course.request.CourseUpdateRequest;
import com.OBE.workflow.feature.course.response.CourseResponse;
import com.OBE.workflow.feature.course.course_version.CourseVersion;
import com.OBE.workflow.feature.course.response.CourseResponseDetail;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "subDepartment", source = "subDepartmentId", qualifiedByName = "mapSubDepartment")
    Course toCourse(CourseCreateRequest request);

    // Những cái trùng tên như id, defaultName, name, credits... MapStruct tự hiểu
    @Mapping(target = "subDepartmentId", source = "course.subDepartment.id")
    @Mapping(target = "versionNumber", source = "version.versionNumber")
    CourseResponse toResponse(Course course, CourseVersion version);

    // --- Mapping cho Detail Response ---
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "defaultName", source = "course.defaultName")
    @Mapping(target = "subDepartmentName", source = "course.subDepartment.name") // Giả định SubDepartment có field name
    @Mapping(target = "versionNumber", source = "version.versionNumber")
    @Mapping(target = "credits", source = "version.credits")
    @Mapping(target = "fromDate", source = "version.fromDate")
    @Mapping(target = "toDate", source = "version.toDate")
    @Mapping(target = "cos", source = "version.cos")
    @Mapping(target = "clos", source = "version.clos")
    @Mapping(target = "assessments", source = "version.assessments")
    // Các phần mapping phức tạp có thể xử lý qua AfterMapping hoặc custom logic nếu cần
    CourseResponseDetail toDetailResponse(Course course, CourseVersion version);

    @Mapping(target = "subDepartment", source = "subDepartmentId", qualifiedByName = "mapSubDepartment")
    void updateCourse(CourseUpdateRequest request, @MappingTarget Course course);

    @Named("mapSubDepartment")
    default SubDepartment mapSubDepartment(String subDepartmentId) {
        if (subDepartmentId == null) return null;
        SubDepartment sd = new SubDepartment();
        sd.setId(subDepartmentId);
        return sd;
    }
}