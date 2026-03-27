package com.OBE.workflow.feature.student_class;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.EducationProgramRepository;
import com.OBE.workflow.feature.school_year.SchoolYear;
import com.OBE.workflow.feature.school_year.SchoolYearRepository;
import com.OBE.workflow.feature.student_class.request.StudentClassCreateRequest;
import com.OBE.workflow.feature.student_class.request.StudentClassFilterRequest;
import com.OBE.workflow.feature.student_class.request.StudentClassUpdateRequest;
import com.OBE.workflow.feature.student_class.response.StudentClassResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentClassService {
    private final StudentClassRepository studentClassRepository;
    private final SchoolYearRepository schoolYearRepository;
    private final EducationProgramRepository educationProgramRepository;

    @Transactional(readOnly = true)
    public Page<StudentClassResponse> getStudentClasses(Pageable pageable, StudentClassFilterRequest filter) {
        // 1. Gộp các điều kiện lọc
        Specification<StudentClass> spec = Specification
                .where(StudentClassSpecification.hasId(filter.getId()))
                .and(StudentClassSpecification.hasName(filter.getName()))
                .and(StudentClassSpecification.hasSchoolYear(filter.getSchoolYearId()))
                .and(StudentClassSpecification.hasEducationProgramId(filter.getEducationProgramId()))
                .and(StudentClassSpecification.hasEducationProgramName(filter.getEducationProgramName()))
                .and(StudentClassSpecification.hasSubDepartmentId(filter.getSubDepartmentId()))
                .and(StudentClassSpecification.hasDepartmentId(filter.getDepartmentId()));

        Page<StudentClass> entityPage = studentClassRepository.findAll(spec, pageable);
        return entityPage.map(StudentClassResponse::fromEntity);
    }

    @Transactional
    public StudentClassResponse createClass(StudentClassCreateRequest request) {
        // 1. Kiểm tra trùng mã lớp (Trả về mã lớp người dùng đã nhập)
        if (studentClassRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED,
                    "Mã lớp [" + request.getId() + "] đã tồn tại trên hệ thống");
        }

        // 2. Kiểm tra niên khóa (Trả về ID niên khóa sai)
        SchoolYear schoolYear = schoolYearRepository.findById(request.getSchoolYearId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy niên khóa với mã: " + request.getSchoolYearId()));

        // 3. Kiểm tra CTĐT (Trả về ID CTĐT sai)
        EducationProgram ep = educationProgramRepository.findById(request.getEducationProgramId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy chương trình đào tạo với mã: " + request.getEducationProgramId()));

        StudentClass entity = StudentClass.builder()
                .id(request.getId())
                .name(request.getName())
                .schoolYear(schoolYear)
                .educationProgram(ep)
                .build();

        return StudentClassResponse.fromEntity(studentClassRepository.save(entity));
    }

    @Transactional
    public StudentClassResponse updateClass(String id, StudentClassUpdateRequest request) {
        // 1. Tìm lớp cũ bằng ID từ PathVariable
        StudentClass entity = studentClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy lớp học cần cập nhật với mã: " + id));

        // 2. Tìm niên khóa mới
        SchoolYear schoolYear = schoolYearRepository.findById(request.getSchoolYearId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Cập nhật thất bại: Không thấy niên khóa " + request.getSchoolYearId()));

        // 3. Tìm CTĐT mới
        EducationProgram ep = educationProgramRepository.findById(request.getEducationProgramId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Cập nhật thất bại: Không thấy CTĐT " + request.getEducationProgramId()));

        // Cập nhật các trường được phép sửa
        entity.setName(request.getName());
        entity.setSchoolYear(schoolYear);
        entity.setEducationProgram(ep);

        return StudentClassResponse.fromEntity(studentClassRepository.save(entity));
    }

    @Transactional
    public void deleteClass(String id) {
        // 1. Kiểm tra sự tồn tại của lớp
        StudentClass entity = studentClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không thể xóa: Không tìm thấy lớp học với mã [" + id + "]"));

        // 2. Kiểm tra ràng buộc dữ liệu (Nếu lớp đã có sinh viên thì không cho xóa)
        if (!entity.getStudents().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION,
                    "Không thể xóa lớp [" + id + "] vì lớp này đã có sinh viên. Vui lòng chuyển sinh viên sang lớp khác trước.");
        }

        // 3. Thực hiện xóa
        studentClassRepository.delete(entity);
    }
}
