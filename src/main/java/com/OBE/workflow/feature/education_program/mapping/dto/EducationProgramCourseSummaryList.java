package com.OBE.workflow.feature.education_program.mapping.dto;
import com.OBE.workflow.feature.course_version.CourseVersion;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationProgramCourseSummaryList {
    String courseId;
    Integer courseVersionNumber;
    List<EducationProgramSummary> educationProgramSummaries;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationProgramSummary{
        String educationProgramId;
        String educationProgramName;
    }

    /**
     * Chuyển đổi từ Entity Course sang DTO
     * Giả định Course có getEducationPrograms() hoặc thông qua bảng trung gian
     */
    public static EducationProgramCourseSummaryList fromEntity(CourseVersion courseVersion) {
        if (courseVersion == null) return null;

        List<EducationProgramSummary> summaries = courseVersion.getProgramCourseDetails().stream()
                .map(detail -> EducationProgramSummary.builder()
                        .educationProgramId(detail.getEducationProgram().getId())
                        .educationProgramName(detail.getEducationProgram().getName())
                        .build())
                .collect(Collectors.toList());

        return EducationProgramCourseSummaryList.builder()
                .courseId(courseVersion.getCourse().getId())
                .courseVersionNumber(courseVersion.getVersionNumber())
                .educationProgramSummaries(summaries)
                .build();
    }
}
