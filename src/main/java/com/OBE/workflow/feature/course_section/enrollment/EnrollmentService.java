package com.OBE.workflow.feature.course_section.enrollment;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_version.assessment.Assessment;
import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.CourseSectionRepository;
import com.OBE.workflow.feature.course_section.grade.Grade;
import com.OBE.workflow.feature.course_section.grade.GradeRequest;
import com.OBE.workflow.feature.student.Student;
import com.OBE.workflow.feature.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;

    @Transactional
    public void addStudentToSection(String studentId, String sectionId) {
        // 1. Check tồn tại
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Sinh viên không tồn tại"));
        CourseSection section = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lớp không tồn tại"));

        // 2. Check xem đã đăng ký chưa để tránh duplicate
        if (enrollmentRepository.existsByStudentIdAndCourseSectionId(studentId, sectionId)) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Sinh viên đã có tên trong lớp này");
        }

        // 3. Tạo enrollment mới
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .courseSection(section)
                .build();
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void removeStudentFromSection(String studentId, String sectionId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseSectionId(studentId, sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bản ghi đăng ký"));
        // Nhờ CascadeType.ALL và orphanRemoval ở Entity,
        // toàn bộ Grade liên quan sẽ bị xóa sạch khi ta xóa Enrollment.
        enrollmentRepository.delete(enrollment);
    }

    @Transactional
    public void updateGrades(Enrollment enrollment, List<GradeRequest> newGrades) {
        // 1. Lấy danh sách mã cột điểm hợp lệ từ CourseVersion của lớp này
        List<String> validCodes = enrollment.getCourseSection()
                .getCourseVersion()
                .getAssessments()
                .stream()
                .map(Assessment::getAssessmentCode) // Giả định Assessment có field này
                .toList();

        // 2. Lọc bỏ hoặc báo lỗi nếu có mã lạ gửi từ Frontend
        for (GradeRequest dto : newGrades) {
            if (!validCodes.contains(dto.getAssessmentCode())) {
                throw new AppException(ErrorCode.INVALID_PARAM,
                        "Mã đánh giá [" + dto.getAssessmentCode() + "] không tồn tại trong cấu hình môn học!");
            }
        }

        List<Grade> currentGrades = enrollment.getGrades();

        // 3. Cập nhật hoặc Thêm mới (Logic cũ đã ổn)
        for (GradeRequest dto : newGrades) {
            currentGrades.stream()
                    .filter(g -> g.getAssessmentCode().equals(dto.getAssessmentCode()))
                    .findFirst()
                    .ifPresentOrElse(
                            existing -> existing.setScore(dto.getScore()),
                            () -> currentGrades.add(Grade.builder()
                                    .assessmentCode(dto.getAssessmentCode())
                                    .score(dto.getScore())
                                    .enrollment(enrollment)
                                    .build())
                    );
        }

        // 4. Đồng bộ hóa: Xóa những điểm không còn trong danh sách mới
        // HOẶC xóa những điểm không còn hợp lệ trong CourseVersion (đề phòng cấu hình môn học thay đổi)
        currentGrades.removeIf(existing ->
                !validCodes.contains(existing.getAssessmentCode()) ||
                        newGrades.stream().noneMatch(dto -> dto.getAssessmentCode().equals(existing.getAssessmentCode()))
        );
    }

    @Transactional(readOnly = true)
    public void validateGradeConsistency(String sectionId) {
        // 1. Lấy danh sách mã cột điểm chuẩn từ CourseVersion của lớp
        CourseSection section = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lớp không tồn tại"));

        List<String> requiredCodes = section.getCourseVersion().getAssessments().stream()
                .map(Assessment::getAssessmentCode)
                .toList();

        // 2. Lấy tất cả sinh viên trong lớp
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseSectionId(sectionId);

        for (Enrollment en : enrollments) {
            List<String> studentGradeCodes = en.getGrades().stream()
                    .map(Grade::getAssessmentCode)
                    .toList();

            // Check thiếu cột điểm
            List<String> missingCodes = requiredCodes.stream()
                    .filter(code -> !studentGradeCodes.contains(code))
                    .toList();

            // Check thừa cột điểm (Cột đã bị xóa khỏi CourseVersion)
            List<String> redundantCodes = studentGradeCodes.stream()
                    .filter(code -> !requiredCodes.contains(code))
                    .toList();

            if (!missingCodes.isEmpty() || !redundantCodes.isEmpty()) {
                String errorMsg = String.format("Sinh viên [%s] không khớp cấu hình điểm. Thiếu: %s, Thừa: %s",
                        en.getStudent().getId(), missingCodes, redundantCodes);
                // Bạn có thể ném Exception hoặc Log lại tùy nhu cầu
                throw new AppException(ErrorCode.INVALID_PARAM, errorMsg);
            }
        }
    }
    @Transactional
    public void syncGradesForSection(String sectionId) {
        CourseSection section = courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Lớp không tồn tại"));

        // 1. Lấy danh sách các mã đánh giá bắt buộc từ cấu hình CourseVersion
        List<String> requiredCodes = section.getCourseVersion().getAssessments().stream()
                .map(Assessment::getAssessmentCode)
                .toList();

        // 2. Lấy danh sách đăng ký của tất cả sinh viên trong lớp
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseSectionId(sectionId);

        for (Enrollment en : enrollments) {
            // Lấy danh sách mã điểm hiện có của sinh viên này
            List<String> currentCodes = en.getGrades().stream()
                    .map(Grade::getAssessmentCode)
                    .toList();

            // 3. Chỉ thêm những cột còn thiếu (Khởi tạo giá trị null)
            // Không xóa những cột thừa (để bảo vệ dữ liệu lịch sử)
            requiredCodes.stream()
                    .filter(code -> !currentCodes.contains(code))
                    .forEach(code -> en.getGrades().add(Grade.builder()
                            .assessmentCode(code)
                            .score(null)
                            .enrollment(en)
                            .build()));
        }
        // 4. Lưu lại toàn bộ thay đổi
        enrollmentRepository.saveAll(enrollments);
    }
}
