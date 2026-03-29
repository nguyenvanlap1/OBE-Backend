package com.OBE.workflow.feature.education_program.program_course_detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramCourseDetailResponse {
    String courseId;
    Integer courseVersionNumber;
    String courseVersionName;
    Integer courseCredit;
    String knowledgeBlockId;
    String knowledgeBlockName; // Thêm tên khối kiến thức cho đầy đủ thông tin

    public static ProgramCourseDetailResponse fromEntity(ProgramCourseDetail entity) {
        if (entity == null) return null;

        var response = ProgramCourseDetailResponse.builder();

        if (entity.getCourseVersion() != null) {
            // Lấy ID từ Course thông qua CourseVersion
            response.courseId(entity.getCourseVersion().getCourse().getId());
            response.courseVersionNumber(entity.getCourseVersion().getVersionNumber());
            response.courseVersionName(entity.getCourseVersion().getName());
            response.courseCredit(entity.getCourseVersion().getCredits());
        }

        if (entity.getKnowledgeBlock() != null) {
            response.knowledgeBlockId(entity.getKnowledgeBlock().getId());
            response.knowledgeBlockName(entity.getKnowledgeBlock().getName());
        }

        return response.build();
    }
}