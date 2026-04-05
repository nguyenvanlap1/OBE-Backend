package com.OBE.workflow.feature.school_year;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.education_program.EducationProgramRepository;
import com.OBE.workflow.feature.student_class.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolYearService {
    private final SchoolYearRepository schoolYearRepository;
    private final EducationProgramRepository educationProgramRepository;
    private final StudentClassRepository studentClassRepository;

    @Transactional(readOnly = true)
    public Page<SchoolYear> getSchoolYears(Pageable pageable, String idFilter) {
        Specification<SchoolYear> spec = Specification.where(SchoolYearSpecification.hasId(idFilter));
        return schoolYearRepository.findAll(spec, pageable);
    }

    @Transactional
    public void deleteSchoolYear(String id) {
        // 1. Kiểm tra tồn tại
        SchoolYear schoolYear = schoolYearRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy niên khóa: " + id));

        // 2. Kiểm tra ràng buộc với Lớp sinh viên (One-to-Many)
        if (studentClassRepository.existsBySchoolYearId(id)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION,
                    "Không thể xóa niên khóa [" + id + "] vì đã có lớp sinh viên thuộc khóa này.");
        }

        // 3. Kiểm tra ràng buộc với Chương trình đào tạo (ManyToMany)
        // Lưu ý: Tên method tùy thuộc vào Repository của EducationProgram
        if (educationProgramRepository.existsBySchoolYearsId(id)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION,
                    "Không thể xóa niên khóa [" + id + "] vì niên khóa này đang nằm trong lộ trình của một Chương trình đào tạo.");
        }

        // 4. Nếu an toàn thì mới xóa
        schoolYearRepository.delete(schoolYear);
    }
}
