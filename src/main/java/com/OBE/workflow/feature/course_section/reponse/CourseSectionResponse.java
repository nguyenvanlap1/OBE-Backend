package com.OBE.workflow.feature.course_section.reponse;

import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentResponse;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionResponse {
    private String id;
    private Integer semesterTerm;
    private String semesterAcademicYear;

    // Thông tin học phần liên quan
    private String courseId;
    private String courseVersionName;
    private Integer versionNumber;

    // Thông tin giảng viên
    private String lecturerId;
    private String lecturerName;

    private String subDepartmentId;
    private String subDepartmentName;

    public static CourseSectionResponse fromEntity(CourseSection entity) {
        if (entity == null) return null;
        SubDepartment subDepartment = entity.getCourseVersion().getCourse().getSubDepartment();
        return CourseSectionResponse.builder()
                .id(entity.getId())
                .semesterTerm(entity.getSemester() != null ? entity.getSemester().getTerm() : null)
                .semesterAcademicYear(entity.getSemester() != null ? entity.getSemester().getAcademicYear() : null)
                // Lấy thông tin từ CourseVersion (thông qua quan hệ ManyToOne)
                .courseId(entity.getCourseVersion() != null ? entity.getCourseVersion().getCourse().getId() : null)
                .courseVersionName(entity.getCourseVersion() != null ? entity.getCourseVersion().getName() : null)
                .versionNumber(entity.getCourseVersion() != null ? entity.getCourseVersion().getVersionNumber() : null)
                // Lấy thông tin từ Lecturer
                .lecturerId(entity.getLecturer() != null ? entity.getLecturer().getId() : null)
                .lecturerName(entity.getLecturer() != null ? entity.getLecturer().getFullName() : null)
                .subDepartmentId(subDepartment.getId())
                .subDepartmentName(subDepartment.getName())
                .build();
    }
}