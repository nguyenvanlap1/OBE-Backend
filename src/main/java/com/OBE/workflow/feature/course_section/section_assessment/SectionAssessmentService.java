package com.OBE.workflow.feature.course_section.section_assessment;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.enrollment.Enrollment;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentRepository;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentRequest;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentResponse;
import com.OBE.workflow.feature.course_section.grade.Grade;
import com.OBE.workflow.feature.course_section.grade.GradeRepository;
import com.OBE.workflow.feature.course_section.grade.GradeRequest;
import com.OBE.workflow.feature.course_section.grade.GradeResponse;
import com.OBE.workflow.feature.course_version.assessment.Assessment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionAssessmentService {

    private final SectionAssessmentRepository sectionAssessmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    @Transactional
    public void syncWithCourseVersion(CourseSection courseSection) {
        // 1. Lấy danh sách Assessment từ Đề cương
        List<Assessment> blueprints = courseSection.getCourseVersion().getAssessments();

        // 2. Số lượng cột điểm ở Chạm PHẢI BẰNG số lượng bài ở Đề cương
        int totalAssessments = blueprints.size();

        // 3. Chạy vòng lặp từ 1 đến đúng số lượng đó (Ví dụ: 1, 2, 3, 4)
        for (long i = 1; i <= totalAssessments; i++) {
            final long currentSTT = i;

            // Tìm xem ở Chạm đã có cột STT này chưa
            Optional<SectionAssessment> existingSA = sectionAssessmentRepository
                    .findByCourseSectionIdAndSectionAssessmentCode(courseSection.getId(), currentSTT);

            // Tìm xem ở Đề cương có ông nào mang mã khớp với STT này không
            Optional<Assessment> blueprintMatch = blueprints.stream()
                    .filter(a -> a.getAssessmentCode().equals(currentSTT))
                    .findFirst();

            if (existingSA.isPresent()) {
                // Đã có: Cập nhật link (Nối nếu khớp mã, Null nếu không khớp)
                SectionAssessment sa = existingSA.get();
                sa.setOriginalAssessment(blueprintMatch.orElse(null));
                sectionAssessmentRepository.save(sa);
                log.debug("Cập nhật cột {}: Link -> {}", currentSTT,
                        blueprintMatch.map(assessment -> "Assessment ID " + assessment.getId()).orElse("NULL"));
            } else {
                // Chưa có: Tạo mới để đảm bảo đủ số lượng và liên tục 1, 2, 3...
                SectionAssessment newSA = SectionAssessment.builder()
                        .courseSection(courseSection)
                        .sectionAssessmentCode(currentSTT)
                        .originalAssessment(blueprintMatch.orElse(null))
                        .build();
                sectionAssessmentRepository.save(newSA);
                log.info("Khởi tạo cột điểm STT {} cho lớp {}", currentSTT, courseSection.getId());
            }
        }
    }

//    @Transactional
//    public EnrollmentResponse updateGrade(EnrollmentRequest request) {
//        // 1. Tìm Enrollment (Đăng ký học phần của sinh viên)
//        // Lưu ý: request.getEnrollmentId() đang là String, hãy ép kiểu nếu DB là Long
//        Long enrollmentId = request.getId();
//        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký học của sinh viên"));
//
//        // 2. Tìm trạm trung chuyển điểm (SectionAssessment) dựa trên mã cột điểm và ID lớp học
//        // Lấy courseSectionId từ enrollment để đảm bảo đúng lớp
//        for(var gradeRequest: request.getGrades()){
//            SectionAssessment sectionAssessment = sectionAssessmentRepository
//                    .findByCourseSectionIdAndSectionAssessmentCode(
//                            enrollment.getCourseSection().getId(),
//                            gradeRequest.getSectionAssessmentCode()
//                    )
//                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Cột điểm STT " + gradeRequest.getSectionAssessmentCode() + " không tồn tại trong lớp này"));
//
//            // 3. Tìm bản ghi điểm (Grade) đã tồn tại hay chưa
//            Grade grade = gradeRepository.findByEnrollmentIdAndSectionAssessmentId(
//                    enrollment.getId(),
//                    sectionAssessment.getId()
//            ).orElse(Grade.builder()
//                    .enrollment(enrollment)
//                    .sectionAssessment(sectionAssessment)
//                    .build());
//
//            // 4. Cập nhật điểm số
//            grade.setScore(gradeRequest.getScore());
//            Grade gradeSaved = gradeRepository.save(grade);
//            if(!enrollment.getGrades().contains(grade)){
//                enrollment.getGrades().add(grade);
//            }
//            log.info("Cập nhật điểm cho SV ID {}: Cột {} -> {} điểm",
//                    gradeSaved.getId(), gradeSaved.getSectionAssessment().getSectionAssessmentCode(), gradeSaved.getScore());
//        }
//                // 5. Trả về Response (Giả định cấu trúc GradeResponse)
//        return EnrollmentResponse.fromEntity(enrollment);
//    }

    @Transactional
    public EnrollmentResponse updateGrade(EnrollmentRequest request) {
        // 1. Tìm Enrollment và fetch luôn danh sách Grades hiện tại để tránh N+1
        Long enrollmentId = request.getId();
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy thông tin đăng ký"));

        // 2. Lấy toàn bộ cấu hình điểm của lớp này một lần duy nhất
        List<SectionAssessment> assessments = sectionAssessmentRepository
                .findByCourseSectionId(enrollment.getCourseSection().getId());
        Map<Long, SectionAssessment> assessmentMap = assessments.stream()
                .collect(Collectors.toMap(SectionAssessment::getSectionAssessmentCode, a -> a));

        // 3. Lấy các bản ghi điểm hiện có của SV này để mapping cho nhanh
        Map<Long, Grade> existingGradesMap = enrollment.getGrades().stream()
                .collect(Collectors.toMap(g -> g.getSectionAssessment().getId(), g -> g));

        for (var gradeRequest : request.getGrades()) {
            SectionAssessment sa = assessmentMap.get(gradeRequest.getSectionAssessmentCode());
            if (sa == null) {
                throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Cột điểm không hợp lệ");
            }

            // Tìm điểm cũ để update hoặc tạo mới
            Grade grade = existingGradesMap.getOrDefault(sa.getId(), new Grade());
            if (grade.getId() == null) {
                grade.setEnrollment(enrollment);
                grade.setSectionAssessment(sa);
                enrollment.getGrades().add(grade); // Sync quan hệ 2 chiều
            }

            grade.setScore(gradeRequest.getScore());
            gradeRepository.save(grade);
        }

        return EnrollmentResponse.fromEntity(enrollment);
    }

    @Transactional
    public EnrollmentResponse updateSingleGrade(Long enrollmentId, Long saCode, Double score) {
        // 1. Tìm Enrollment (Thông tin học tập của SV)
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy thông tin đăng ký của sinh viên"));

        // 2. Tìm đúng cấu hình cột điểm (SectionAssessment) của lớp này dựa trên saCode
        SectionAssessment sa = sectionAssessmentRepository
                .findByCourseSectionIdAndSectionAssessmentCode(enrollment.getCourseSection().getId(), saCode)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy cấu hình cột điểm " + saCode));

        // 3. Kiểm tra xem đã có bản ghi điểm (Grade) cho cột này chưa
        Grade grade = gradeRepository.findByEnrollmentIdAndSectionAssessmentId(enrollment.getId(), sa.getId())
                .orElseGet(() -> {
                    // Nếu chưa có thì tạo mới và nối quan hệ
                    Grade newGrade = Grade.builder()
                            .enrollment(enrollment)
                            .sectionAssessment(sa)
                            .build();
                    enrollment.getGrades().add(newGrade);
                    return newGrade;
                });

        // 4. Cập nhật điểm số
        grade.setScore(score);
        gradeRepository.save(grade);

        log.info("Cập nhật điểm đơn lẻ: SV {}, Cột {}, Điểm {}", enrollment.getStudent().getFullName(), saCode, score);

        return EnrollmentResponse.fromEntity(enrollment);
    }
}