package com.OBE.workflow.feature.course_section;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_section.enrollment.Enrollment;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentRepository;
import com.OBE.workflow.feature.course_section.enrollment.EnrollmentService;
import com.OBE.workflow.feature.course_section.grade.GradeRequest;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponse;
import com.OBE.workflow.feature.course_section.reponse.CourseSectionResponseDetail;
import com.OBE.workflow.feature.course_section.request.CourseSectionCreateRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionFilterRequest;
import com.OBE.workflow.feature.course_section.request.CourseSectionUpdateRequest;
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

import java.util.List;

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

    @Transactional(readOnly = true)
    public Page<CourseSectionResponse> getCourseSections(Pageable pageable, CourseSectionFilterRequest filter) {
        Specification<CourseSection> spec = CourseSectionSpecification.filterBy(filter);
        Page<CourseSection> entityPage = courseSectionRepository.findAll(spec, pageable);
        return entityPage.map(CourseSectionResponse::fromEntity);
    }

    /**
     * Lấy chi tiết một lớp học phần theo ID
     * Bao gồm: Thông tin học phần, Giảng viên, Học kỳ, Cấu hình điểm và Danh sách sinh viên
     */
    @Transactional(readOnly = true)
    public CourseSectionResponseDetail getCourseSectionDetail(String id) {
        // 1. Tìm Entity, ném lỗi nếu không tồn tại
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy lớp học phần với mã: " + id));

        // 2. Chuyển đổi sang DTO
        // Hàm fromEntity bạn đã viết trong CourseSectionResponse đã xử lý map
        // cả AssessmentResponses và EnrollmentResponses (kèm điểm số sinh viên).
        return CourseSectionResponseDetail.fromEntity(courseSection);
    }

    @Transactional
    public CourseSectionResponse createCourseSection(CourseSectionCreateRequest request) {
        // 1. Kiểm tra trùng mã lớp
        if (courseSectionRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Lớp học phần " + request.getId() + " đã tồn tại");
        }

        // 2. Tìm hoặc tạo Semester (Học kỳ)
        Semester semester = semesterRepository.findByTermAndAcademicYear(request.getSemesterTerm(), request.getSemesterAcademicYear())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học kỳ yêu cầu"));

        // 3. Tìm CourseVersion (Khóa phức hợp: ma_hoc_phan + so_thu_tu)
        CourseVersion courseVersion = courseVersionRepository.findByCourseIdAndVersionNumber(request.getCourseId(), request.getVersionNumber())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần"));

        // 4. Tìm giảng viên
        Lecturer lecturer = lecturerRepository.findById(request.getLecturerId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy giảng viên"));

        // 5. Build Entity
        CourseSection courseSection = CourseSection.builder()
                .id(request.getId())
                .semester(semester)
                .courseVersion(courseVersion)
                .lecturer(lecturer)
                .build();

        return CourseSectionResponse.fromEntity(courseSectionRepository.save(courseSection));
    }

    @Transactional
    public CourseSectionResponse updateCourseSection(String id, CourseSectionUpdateRequest request) {
        CourseSection existingSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy lớp học phần"));

        // Cập nhật học kỳ nếu thay đổi
        if (!existingSection.getSemester().getTerm().equals(request.getSemesterTerm()) ||
                !existingSection.getSemester().getAcademicYear().equals(request.getSemesterAcademicYear())) {
            Semester newSemester = semesterRepository.findByTermAndAcademicYear(request.getSemesterTerm(), request.getSemesterAcademicYear())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học kỳ mới không tồn tại"));
            existingSection.setSemester(newSemester);
        }

        // Cập nhật giảng viên
        if (!existingSection.getLecturer().getId().equals(request.getLecturerId())) {
            Lecturer newLecturer = lecturerRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Giảng viên mới không tồn tại"));
            existingSection.setLecturer(newLecturer);
        }

        // Cập nhật phiên bản học phần
        if (!existingSection.getCourseVersion().getCourse().getId().equals(request.getCourseId()) ||
                !existingSection.getCourseVersion().getVersionNumber().equals(request.getVersionNumber())) {
            CourseVersion newVersion = courseVersionRepository.findByCourseIdAndVersionNumber(request.getCourseId(), request.getVersionNumber())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Phiên bản học phần mới không tồn tại"));
            existingSection.setCourseVersion(newVersion);
        }

        existingSection.setId(request.getId());

        return CourseSectionResponse.fromEntity(courseSectionRepository.save(existingSection));
    }

    @Transactional
    public void deleteCourseSection(String id) {
        if (!courseSectionRepository.existsById(id)) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy lớp để xóa");
        }
        courseSectionRepository.deleteById(id);
    }

    // --- QUẢN LÝ SINH VIÊN & ĐIỂM SỐ (Bổ sung mới) ---

    /**
     * Thêm sinh viên vào lớp và tự động khởi tạo khung điểm null cho sinh viên đó.
     */
    @Transactional
    public void addStudentToSection(String studentId, String sectionId) {
        // 1. Thực hiện nghiệp vụ thêm sinh viên
        enrollmentService.addStudentToSection(studentId, sectionId);
        // 2. Tự động đồng bộ khung điểm ngay lập tức để sinh viên có đủ cột điểm theo CourseVersion
        enrollmentService.syncGradesForSection(sectionId);
        log.info("Đã thêm sinh viên {} vào lớp {} và khởi tạo khung điểm.", studentId, sectionId);
    }

    /**
     * Xóa sinh viên khỏi lớp (Tự động xóa sạch điểm liên quan nhờ orphanRemoval)
     */
    @Transactional
    public void removeStudentFromSection(String studentId, String sectionId) {
        enrollmentService.removeStudentFromSection(studentId, sectionId);
        log.info("Đã xóa sinh viên {} khỏi lớp {}.", studentId, sectionId);
    }

    /**
     * Cập nhật điểm cho một sinh viên cụ thể trong lớp.
     */
    @Transactional
    public void updateStudentGrades(String studentId, String sectionId, List<GradeRequest> grades) {
        // Truy vấn trực tiếp từ Repo, dùng orElseThrow để bắt lỗi ngay lập tức
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseSectionId(studentId, sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Sinh viên chưa đăng ký lớp này"));

        // Sau khi có Entity, ta mới đẩy cho Service xử lý logic so sánh/cập nhật phức tạp
        enrollmentService.updateGrades(enrollment, grades);
        log.info("Cập nhật điểm thành công cho SV: {} tại lớp: {}", studentId, sectionId);
    }

    /**
     * Đồng bộ khung điểm cho tất cả sinh viên trong lớp (Dùng khi cấu hình môn học thay đổi).
     */
    @Transactional
    public void syncAllGrades(String sectionId) {
        enrollmentService.syncGradesForSection(sectionId);
    }

    /**
     * Kiểm tra xem có sinh viên nào bị lệch cột điểm so với cấu hình không.
     */
    @Transactional(readOnly = true)
    public void validateGrades(String sectionId) {
        enrollmentService.validateGradeConsistency(sectionId);
    }
}
