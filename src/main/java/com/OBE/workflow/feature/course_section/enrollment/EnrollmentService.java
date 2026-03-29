package com.OBE.workflow.feature.course_section.enrollment;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.CourseSectionRepository;
import com.OBE.workflow.feature.student.Student;
import com.OBE.workflow.feature.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;

    @Transactional
    public EnrollmentResponse addStudentToSection(String studentId, String sectionId) {
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

        return EnrollmentResponse.fromEntity(enrollment);
    }

    @Transactional
    public void removeStudentFromSection(String studentId, String sectionId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseSectionId(studentId, sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bản ghi đăng ký"));
        enrollmentRepository.delete(enrollment);
    }
}
