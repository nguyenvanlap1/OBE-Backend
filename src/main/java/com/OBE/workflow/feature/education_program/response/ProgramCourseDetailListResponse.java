package com.OBE.workflow.feature.education_program.response;

import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramCourseDetailListResponse {
    private String id;
    private List<ProgramCourseDetailResponse> programCourseDetailResponses;

    public static ProgramCourseDetailListResponse fromEntity(EducationProgram entity) {
        if (entity == null) return null;

        // Chuyển đổi danh sách chi tiết học phần bằng Stream
        List<ProgramCourseDetailResponse> details = new ArrayList<>();
        if (entity.getCourseDetails() != null) {
            details = entity.getCourseDetails().stream()
                    .map(ProgramCourseDetailResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return ProgramCourseDetailListResponse.builder()
                .id(entity.getId())
                .programCourseDetailResponses(details)
                .build();
    }
}