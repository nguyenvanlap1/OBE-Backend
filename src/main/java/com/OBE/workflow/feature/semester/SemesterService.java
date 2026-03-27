package com.OBE.workflow.feature.semester;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;

    @Transactional
    public Semester createSemester(SemesterRequest request) {
        // 1. Kiểm tra xem học kỳ này đã tồn tại trong năm học đó chưa
        if (semesterRepository.existsByTermAndAcademicYear(request.getTerm(), request.getAcademicYear())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED,
                    "Học kỳ " + request.getTerm() + " của năm học " + request.getAcademicYear() + " đã tồn tại.");
        }

        // 2. Map từ Request sang Entity
        Semester semester = Semester.builder()
                .term(request.getTerm())
                .academicYear(request.getAcademicYear())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return semesterRepository.save(semester);
    }

    @Transactional
    public Semester updateSemester(Long id, SemesterRequest request) {
        // 1. Tìm học kỳ cần sửa
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học kỳ"));

        // 2. Kiểm tra nếu thay đổi term/year thì có bị trùng với bản ghi khác không
        if (!semester.getTerm().equals(request.getTerm()) || !semester.getAcademicYear().equals(request.getAcademicYear())) {
            if (semesterRepository.existsByTermAndAcademicYear(request.getTerm(), request.getAcademicYear())) {
                throw new AppException(ErrorCode.ENTITY_EXISTED, "Thông tin học kỳ bị trùng lặp với dữ liệu đã có.");
            }
        }

        // 3. Cập nhật thông tin
        semester.setTerm(request.getTerm());
        semester.setAcademicYear(request.getAcademicYear());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());

        return semesterRepository.save(semester);
    }

    @Transactional
    public void deleteSemester(Long id) {
        if (!semesterRepository.existsById(id)) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học kỳ để xóa");
        }
        semesterRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Semester> getSemesters(Pageable pageable, SemesterFilterRequest filter) {
        Specification<Semester> spec = SemesterSpecification.filterBy(filter);
        return semesterRepository.findAll(spec, pageable);
    }
}