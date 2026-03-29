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
public class CourseSectionGradeResponse {
    private String id;
    private List<AssessmentResponse> assessmentResponses;
    private List<SectionAssessmentResponse> sectionAssessmentResponses;
    private List<EnrollmentResponse> enrollmentResponses;

    public static CourseSectionGradeResponse fromEntity(CourseSection entity) {
        if (entity == null) return null;
        // --- ĐOẠN CODE LOG KIỂM TRA ---
        System.out.println("=== DEBUG COURSE SECTION ===");
        System.out.println("ID Lớp học phần: " + entity.getId());

        if (entity.getSectionAssessments() == null) {
            System.out.println("List sectionAssessments đang bị NULL hoàn toàn.");
        } else {
            System.out.println("Số lượng SectionAssessment tìm thấy: " + entity.getSectionAssessments().size());
            entity.getSectionAssessments().forEach(sa -> {
                System.out.println(" - SA ID: " + sa.getId() + " | Code: " + sa.getSectionAssessmentCode());
            });
        }
        System.out.println("==============================");
        SubDepartment subDepartment = entity.getCourseVersion().getCourse().getSubDepartment();
        return CourseSectionGradeResponse.builder()
                .id(entity.getId())
                .assessmentResponses(entity.getCourseVersion().getAssessments().stream().map(a
                        -> AssessmentResponse.builder()
                        .assessmentCode(a.getAssessmentCode())
                        .name(a.getName())
                        .regulation(a.getRegulation())
                        .weight(a.getWeight()).build()).toList())
                .sectionAssessmentResponses(entity.getSectionAssessments().stream().map(a->
                        SectionAssessmentResponse.builder()
                                .id(a.getId())
                                .sectionAssessmentCode(a.getSectionAssessmentCode())
                                .build()).toList())
                .enrollmentResponses(entity.getEnrollments().stream().map(EnrollmentResponse::fromEntity).toList())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentResponse{
        private Long assessmentCode;
        private String name; // Ví dụ: Thi lý thuyết cuối kỳ, Bài tập...
        private String regulation; // Ví dụ: Bắt buộc
        private Double weight; // Ví dụ: 0.5 (tương đương 50%)
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionAssessmentResponse{
        private Long id;
        private Long sectionAssessmentCode;
    }
}
