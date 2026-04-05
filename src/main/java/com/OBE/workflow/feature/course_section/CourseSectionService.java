package com.OBE.workflow.feature.course_section;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_section.CourseSection;
import com.OBE.workflow.feature.course_section.CourseSectionRepository;
import com.OBE.workflow.feature.course_section.CourseSectionSpecification;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentRepository;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentRequest;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentResponse;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentService;
import com.OBE.workflow.feature.course_section.grade.GradeRequest;
import com.OBE.workflow.feature.course_section.grade.GradeResponse;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionGradeResponse;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponse;
import com.OBE.workflow.feature.course_section.request.CourseSectionCreateRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionFilterRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionUpdateRequest;
import com.OBE.workflow.feature.course_section.section_assessment.SectionAssessmentRepository;
import com.OBE.workflow.feature.course_section.section_assessment.SectionAssessmentService;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.CourseVersionRepository;
import com.OBE.workflow.feature.lecturer.Lecturer;
import com.OBE.workflow.feature.lecturer.LecturerRepository;
import com.OBE.workflow.feature.semester.Semester;
import com.OBE.workflow.feature.semester.SemesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSectionService {

    private final CourseSectionRepository courseSectionRepository;
    private final SemesterRepository semesterRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LecturerRepository lecturerRepository;
    private final EnrollmentService enrollmentService;
    private final SectionAssessmentRepository assessmentRepository;

    // 1. Tiêm SectionAssessmentService vào để xử lý điểm và khung điểm
    private final SectionAssessmentService sectionAssessmentService;

    @Transactional(readOnly = true)
    public Page<CourseSectionResponse> getCourseSections(Pageable pageable, CourseSectionFilterRequest filter) {
        Specification<CourseSection> spec = CourseSectionSpecification.filterBy(filter);
        Page<CourseSection> entityPage = courseSectionRepository.findAll(spec, pageable);
        return entityPage.map(CourseSectionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CourseSectionGradeResponse getCourseGradeResponse(String id) {
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy lớp học phần với mã: " + id));
        return CourseSectionGradeResponse.fromEntity(courseSection);
    }

    @Transactional
    public CourseSectionResponse createCourseSection(CourseSectionCreateRequest request) {
        if (courseSectionRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Lớp học phần " + request.getId() + " đã tồn tại");
        }

        Semester semester = semesterRepository.findByTermAndAcademicYear(request.getSemesterTerm(), request.getSemesterAcademicYear())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học kỳ yêu cầu"));

        CourseVersion courseVersion = courseVersionRepository.findByCourseIdAndVersionNumber(request.getCourseId(), request.getVersionNumber())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần"));

        Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy giảng viên"));

        CourseSection courseSection = CourseSection.builder()
                .id(request.getId())
                .semester(semester)
                .courseVersion(courseVersion)
                .lecturer(lecturer)
                .build();

        CourseSection savedSection = courseSectionRepository.save(courseSection);

        // 2. Tự động đồng bộ khung điểm (SectionAssessment) ngay khi tạo lớp
        sectionAssessmentService.syncWithCourseVersion(savedSection);

        return CourseSectionResponse.fromEntity(savedSection);
    }

    @Transactional
    public CourseSectionResponse updateCourseSection(String id, CourseSectionUpdateRequest request) {
        CourseSection existingSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy lớp học phần"));

        // Kiểm tra xem có thay đổi CourseVersion không
        boolean isVersionChanged = !existingSection.getCourseVersion().getCourse().getId().equals(request.getCourseId()) ||
                !existingSection.getCourseVersion().getVersionNumber().equals(request.getVersionNumber());

        // Cập nhật các thông tin cơ bản...
        if (!existingSection.getSemester().getTerm().equals(request.getSemesterTerm()) ||
                !existingSection.getSemester().getAcademicYear().equals(request.getSemesterAcademicYear())) {
            Semester newSemester = semesterRepository.findByTermAndAcademicYear(request.getSemesterTerm(), request.getSemesterAcademicYear())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học kỳ mới không tồn tại"));
            existingSection.setSemester(newSemester);
        }

        if (!existingSection.getLecturer().getId().equals(request.getLecturerId())) {
            Lecturer newLecturer = lecturerRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Giảng viên mới không tồn tại"));
            existingSection.setLecturer(newLecturer);
        }

        if (isVersionChanged) {
            CourseVersion newVersion = courseVersionRepository.findByCourseIdAndVersionNumber(request.getCourseId(), request.getVersionNumber())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Phiên bản học phần mới không tồn tại"));
            existingSection.setCourseVersion(newVersion);
        }

        CourseSection updatedSection = courseSectionRepository.save(existingSection);

        // 3. Nếu phiên bản học phần thay đổi, phải đồng bộ lại khung điểm
        if (isVersionChanged) {
            sectionAssessmentService.syncWithCourseVersion(updatedSection);
        }

        return CourseSectionResponse.fromEntity(updatedSection);
    }

    @Transactional
    public void deleteCourseSection(String id) {
        if (!courseSectionRepository.existsById(id)) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy lớp để xóa");
        }
        courseSectionRepository.deleteById(id);
    }

    // --- QUẢN LÝ SINH VIÊN & ĐIỂM SỐ ---

    /**
     * Cập nhật điểm số cho một sinh viên cụ thể trong lớp
     */
    @Transactional
    public EnrollmentResponse updateStudentGrade(EnrollmentRequest request) {
        // Ủy quyền (delegate) việc xử lý điểm cho SectionAssessmentService
        return sectionAssessmentService.updateGrade(request);
    }

    @Transactional
    public EnrollmentResponse addStudentToSection(String studentId, String sectionId) {
        EnrollmentResponse enrollmentResponse = enrollmentService.addStudentToSection(studentId, sectionId);
        log.info("Đã thêm sinh viên {} vào lớp {} và sẵn sàng nhập điểm.", studentId, sectionId);
        return enrollmentResponse;
    }

    @Transactional
    public void removeStudentFromSection(String studentId, String sectionId) {
        enrollmentService.removeStudentFromSection(studentId, sectionId);
    }

    public CourseSectionResponse getCourseSection(String id) {
        return CourseSectionResponse.fromEntity(courseSectionRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy lớp học phần vơi mã: "+ id)
        ));
    }

    /**
     * Cập nhật một đầu điểm đơn lẻ cho sinh viên (Phục vụ AG Grid Edit)
     */
    @Transactional
    public EnrollmentResponse updateSingleStudentGrade(String sectionId, Long enrollmentId, Long saCode, Double score) {
        log.info("Yêu cầu cập nhật điểm đơn lẻ: Enrollment {}, Cột {}, Điểm {}", enrollmentId, saCode, score);

        // Kiểm tra điểm hợp lệ (0-10) trước khi xử lý
        if (score < 0 || score > 10) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Điểm số phải nằm trong khoảng từ 0 đến 10");
        }

        // Ủy quyền cho SectionAssessmentService xử lý logic nghiệp vụ
        return sectionAssessmentService.updateSingleGrade(sectionId, enrollmentId, saCode, score);
    }

    /**
     * Lấy ID Bộ môn quản lý lớp học phần này
     */
    @Transactional(readOnly = true)
    public String getSubDepartmentIdBySection(String id) {
        return courseSectionRepository.findById(id)
                .map(section -> section.getCourseVersion().getCourse().getSubDepartment().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy thông tin Bộ môn cho lớp học phần: " + id));
    }

    /**
     * Lấy ID Khoa quản lý lớp học phần này
     */
    @Transactional(readOnly = true)
    public String getDepartmentIdBySection(String id) {
        return courseSectionRepository.findById(id)
                .map(section -> section.getCourseVersion().getCourse().getSubDepartment().getDepartment().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy thông tin Khoa cho lớp học phần: " + id));
    }
}