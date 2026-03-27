package com.OBE.workflow.feature.course_version.response;

import com.OBE.workflow.feature.course_version.CourseVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseVersionResponseDetail {
    // --- Thông tin định danh học phần ---
    private String courseId;
    // --- Thông tin phân cấp (Department & SubDepartment) ---
    private String subDepartmentId;
    private String subDepartmentName;
    private String departmentId;      // <--- BỔ SUNG
    private String departmentName;    // <--- BỔ SUNG

    // --- Thông tin phiên bản & Tín chỉ ---
    private Integer versionNumber;
    private String name;
    private Integer credits;
    private LocalDate fromDate;
    private LocalDate toDate;

    // --- Các danh sách liên quan (OBE Components) ---
    private List<CoResponse> cos;
    private List<CloResponse> clos;
    private List<AssessmentResponse> assessments;
    private List<CoCloMappingResponse> coCloMappings;
    private List<AssessmentCloMappingResponse> assessmentCloMappings;

    @Data
    @Builder
    public static class CoResponse {
        private Long id;
        private String coCode;
        private String content;
    }

    @Data
    @Builder
    public static class CloResponse {
        private Long id;
        private String cloCode;
        private String content;
    }

    @Data
    @Builder
    public static class AssessmentResponse {
        private Long id;
        private String assessmentCode;
        private String name;
        private String regulation;
        private Double weight;     // Trọng số (ví dụ: 0.2 cho chuyên cần)
    }

    // Các class Mapping giữ nguyên vì đã đủ ID và Weight để vẽ Matrix
    @Data
    @Builder
    public static class CoCloMappingResponse {
        private Long coId;
        private Long cloId;
        private String coCode;
        private String cloCode;
        private Double weight;
    }

    @Data
    @Builder
    public static class AssessmentCloMappingResponse {
        private Long assessmentId;
        private Long cloId;
        private String assessmentCode;
        private String assessmentName;
        private String cloCode;
        private Double weight;
    }

    public static CourseVersionResponseDetail fromEntity(CourseVersion entity) {
        if (entity == null) return null;

        var course = entity.getCourse();
        var subDept = course.getSubDepartment();
        var dept = subDept.getDepartment();

        return CourseVersionResponseDetail.builder()
                // 1. Thông tin cơ bản
                .courseId(course.getId())
                .name(entity.getName())
                .subDepartmentId(subDept.getId())
                .subDepartmentName(subDept.getName())
                .departmentId(dept.getId())
                .departmentName(dept.getName())
                .versionNumber(entity.getVersionNumber())
                .credits(entity.getCredits())
                .fromDate(entity.getFromDate())
                .toDate(entity.getToDate())

                // 2. Chuyển đổi danh sách CO (Course Outcomes)
                .cos(entity.getCos().stream()
                        .map(co -> CoResponse.builder()
                                .id(co.getId())
                                .coCode(co.getCoCode())
                                .content(co.getContent())
                                .build())
                        .toList())

                // 3. Chuyển đổi danh sách CLO (Course Learning Outcomes)
                .clos(entity.getClos().stream()
                        .map(clo -> CloResponse.builder()
                                .id(clo.getId())
                                .cloCode(clo.getCloCode())
                                .content(clo.getContent())
                                .build())
                        .toList())

                // 4. Chuyển đổi danh sách Assessment (Đánh giá)
                .assessments(entity.getAssessments().stream()
                        .map(as -> AssessmentResponse.builder()
                                .id(as.getId())
                                .assessmentCode(as.getAssessmentCode()) // Giả định Entity Assessment có getCode()
                                .name(as.getName())
                                .regulation(as.getRegulation())
                                .weight(as.getWeight())
                                .build())
                        .toList())

                // 5. Mapping CO-CLO Matrix
                // Logic: Duyệt qua từng CO, lấy danh sách mapping CLO bên trong chúng
                .coCloMappings(entity.getCos().stream()
                        .flatMap(co -> co.getCoCloMappings().stream() // Giả định co.getCoCloMappings() tồn tại
                                .map(m -> CoCloMappingResponse.builder()
                                        .cloId(m.getClo().getId())
                                        .coId(co.getId())
                                        .coCode(co.getCoCode())
                                        .cloCode(m.getClo().getCloCode())
                                        .weight(m.getWeight())
                                        .build()))
                        .toList())

                // 6. Mapping Assessment-CLO Matrix
                .assessmentCloMappings(entity.getAssessments().stream()
                        .flatMap(as -> as.getAssessmentCloMappings().stream() // Giả định as.getAssessmentCloMappings() tồn tại
                                .map(m -> AssessmentCloMappingResponse.builder()
                                        .assessmentId(as.getId())
                                        .cloId(m.getClo().getId())
                                        .assessmentCode(as.getAssessmentCode())
                                        .assessmentName(as.getName())
                                        .cloCode(m.getClo().getCloCode())
                                        .weight(m.getWeight())
                                        .build()))
                        .toList())
                .build();
    }
}