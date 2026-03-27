package com.OBE.workflow.feature.course_section.reponse;

import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentResponse;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionResponseDetail {
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


    private List<AssessmentResponse> assessmentResponses;
    private List<EnrollmentResponse> enrollmentResponses;

    public static CourseSectionResponseDetail fromEntity(CourseSection entity) {
        if (entity == null) return null;
        SubDepartment subDepartment = entity.getCourseVersion().getCourse().getSubDepartment();
        return CourseSectionResponseDetail.builder()
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
                .assessmentResponses(entity.getCourseVersion().getAssessments().stream().map(a
                        -> AssessmentResponse.builder()
                        .assessmentCode(a.getAssessmentCode())
                        .name(a.getName())
                        .regulation(a.getRegulation())
                        .weight(a.getWeight()).build()).toList())
                .enrollmentResponses(entity.getEnrollments().stream().map(EnrollmentResponse::fromEntity).toList())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentResponse{
        private String assessmentCode;
        private String name; // Ví dụ: Thi lý thuyết cuối kỳ, Bài tập...
        private String regulation; // Ví dụ: Bắt buộc
        private Double weight; // Ví dụ: 0.5 (tương đương 50%)
    }
}
