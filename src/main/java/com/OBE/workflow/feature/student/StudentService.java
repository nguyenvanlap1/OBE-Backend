package com.OBE.workflow.feature.student;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.student.request.StudentCreateRequest;
import com.OBE.workflow.feature.student.request.StudentFilterRequest;
import com.OBE.workflow.feature.student.request.StudentUpdateRequest;
import com.OBE.workflow.feature.student.response.StudentResponse;
import com.OBE.workflow.feature.student_class.StudentClass;
import com.OBE.workflow.feature.student_class.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;

    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudents(Pageable pageable, StudentFilterRequest filter) {
        // 1. Gộp các điều kiện lọc dựa trên StudentSpecification
        Specification<Student> spec = Specification
                .where(StudentSpecification.hasId(filter.getId()))
                .and(StudentSpecification.hasFullName(filter.getFullName()))
                .and(StudentSpecification.hasGender(filter.getGender()))
                .and(StudentSpecification.hasStudentClasses(filter.getStudentClassesId()))
                .and(StudentSpecification.hasEducationProgramId(filter.getEducationProgramId()))
                .and(StudentSpecification.hasEducationProgramNames(filter.getEducationProgramName()))
                .and(StudentSpecification.hasSubDepartmentId(filter.getSubDepartmentId()))
                .and(StudentSpecification.hasDepartmentId(filter.getDepartmentId()));

        // 2. Thực hiện truy vấn với Specification và Pageable
        // Lưu ý: studentRepository cần extends JpaSpecificationExecutor<Student>
        Page<Student> entityPage = studentRepository.findAll(spec, pageable);

        // 3. Map từ Entity sang Response DTO
        return entityPage.map(StudentResponse::fromEntity);
    }

    @Transactional
    public StudentResponse updateStudent(String id, StudentUpdateRequest request) {
        // 1. Tìm sinh viên hiện tại
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy sinh viên với mã: " + id));

        // 2. Cập nhật các thông tin cơ bản
        student.setFullName(request.getFullName());
        student.setGender(request.getGender());

        // 3. Xử lý cập nhật quan hệ ManyToMany (Sử dụng Helper Methods)

        // BƯỚC A: Gỡ sinh viên khỏi toàn bộ các lớp hiện tại
        // Sử dụng copy (new HashSet) để tránh ConcurrentModificationException khi duyệt Set
        new HashSet<>(student.getStudentClasses()).forEach(clazz -> {
            clazz.removeStudent(student);
        });

        // BƯỚC B: Thêm sinh viên vào các lớp mới từ request
        request.getStudentClassesId().forEach(classId -> {
            StudentClass newClass = studentClassRepository.findById(classId)
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                            "Không tìm thấy lớp học với mã: " + classId));

            // Gọi helper từ phía StudentClass để Hibernate nhận diện thay đổi
            newClass.addStudent(student);
        });

        // 4. Lưu thay đổi
        // Lúc này Hibernate sẽ tự động so sánh và phát lệnh INSERT/DELETE vào bảng 'lop_sinh_vien_chi_tiet'
        Student updatedStudent = studentRepository.save(student);
        return StudentResponse.fromEntity(updatedStudent);
    }

    @Transactional
    public StudentResponse createStudent(StudentCreateRequest request) {
        // 1. Kiểm tra trùng mã sinh viên
        if (studentRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED,
                    "Mã sinh viên [" + request.getId() + "] đã tồn tại trên hệ thống");
        }

        // 2. Kiểm tra và tìm danh sách các lớp học
        // Sử dụng Set để đảm bảo tính duy nhất và findById cho từng mã lớp
        Set<StudentClass> studentClasses = request.getStudentClassesId().stream()
                .map(classId -> studentClassRepository.findById(classId)
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                                "Không tìm thấy lớp học với mã: " + classId)))
                .collect(Collectors.toSet());

        // 3. Khởi tạo Entity Student từ Request sử dụng Builder
        Student student = Student.builder()
                .id(request.getId())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .studentClasses(studentClasses) // Gán tập hợp lớp đã tìm thấy
                .build();

        // 4. Lưu vào database và trả về Response DTO
        Student savedStudent = studentRepository.save(student);

        return StudentResponse.fromEntity(savedStudent);
    }

    @Transactional
    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy sinh viên với mã: " + id));

        // Xóa các mối quan hệ với lớp học trong bảng trung gian
        student.getStudentClasses().clear();

        // Thực hiện xóa thực thể sinh viên
        studentRepository.delete(student);
    }
}