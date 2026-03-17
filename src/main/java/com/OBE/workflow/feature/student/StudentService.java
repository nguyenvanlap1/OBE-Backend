package com.OBE.workflow.feature.student;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.auth.AccountRepository;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.EducationProgramRepository;
import com.OBE.workflow.feature.student.request.StudentFilterRequest;
import com.OBE.workflow.feature.student.request.StudentRequest;
import com.OBE.workflow.other_entity_repo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final EducationProgramRepository educationProgramRepository;
    private final StudentMapper studentMapper;

    @Value("${obe.system-admin.username}")
    private String systemAdminUser;

    @Transactional(readOnly = true)
    public Page<Student> getStudents(Pageable pageable, StudentFilterRequest filter) {
        Specification<Student> spec = Specification
                .where(StudentSpecification.hasId(filter.getId()))
                .and(StudentSpecification.hasFullName(filter.getFullName()))
                .and(StudentSpecification.hasGender(filter.getGender()))
                .and(StudentSpecification.hasEducationProgramId(filter.getEducationProgramId()));

        return studentRepository.findAll(spec, pageable);
    }

    @Transactional
    public Student createStudent(StudentRequest request) {
        if (personRepository.existsById(request.getId())
                || systemAdminUser.equals(request.getId())) {
            throw new AppException(ErrorCode.USER_EXISTED, "Mã sinh viên đã tồn tại hoặc trùng với admin hệ thống");
        }

        Student student = studentMapper.toEntity(request);

        // Xử lý gán danh sách chương trình đào tạo (Many-to-Many)
        setEducationPrograms(student, request.getEducationProgramIds());

        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(String id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy sinh viên để cập nhật"));

        studentMapper.updateStudent(student, request);

        // Cập nhật lại danh sách chương trình đào tạo
        setEducationPrograms(student, request.getEducationProgramIds());

        return studentRepository.save(student);
    }

    private void setEducationPrograms(Student student, Set<String> programIds) {
        if (programIds != null && !programIds.isEmpty()) {
            List<EducationProgram> programs = educationProgramRepository.findAllById(programIds);
            if (programs.size() != programIds.size()) {
                throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Một hoặc nhiều chương trình đào tạo không tồn tại");
            }
            student.setEducationPrograms(new HashSet<>(programs));
        }
    }

    @Transactional
    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy sinh viên để xóa"));

        if (accountRepository.existsByPerson(student)) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                    "Không thể xóa sinh viên này vì đã được cấp tài khoản hệ thống.");
        }

        studentRepository.delete(student);
    }
}