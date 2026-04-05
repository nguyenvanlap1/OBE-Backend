package com.OBE.workflow.feature.lecturer;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.conmon.authorization.account.AccountRepository;
import com.OBE.workflow.feature.lecturer.request.LecturerFilterRequest;
import com.OBE.workflow.feature.lecturer.request.LecturerRequest;
import com.OBE.workflow.feature.lecturer.response.LecturerResponse;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import com.OBE.workflow.conmon.authorization.account.person.PersonRepository;
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
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final LecturerMapper lecturerMapper;

    @Value("${obe.system-admin.username}")
    private String systemAdminUser;

    @Transactional(readOnly = true)
    public Page<LecturerResponse> getLecturers(Pageable pageable, LecturerFilterRequest filter) {
        // 1. Xây dựng Specification dựa trên filter
        Specification<Lecturer> spec = Specification
                .where(LecturerSpecification.hasId(filter.getId()))
                .and(LecturerSpecification.hasFullName(filter.getFullName()))
                .and(LecturerSpecification.hasGender(filter.getGender()))
                .and(LecturerSpecification.hasSubDepartmentId(filter.getSubDepartmentIds()));

        // 2. Truy vấn lấy Page các Entity từ Database
        Page<Lecturer> lecturerPage = lecturerRepository.findAll(spec, pageable);

        // 3. Chuyển đổi Page<Entity> sang Page<DTO> bằng phương thức map
        // Sử dụng Method Reference để code ngắn gọn và chuyên nghiệp
        return lecturerPage.map(LecturerResponse::fromEntity);
    }

    @Transactional
    public LecturerResponse createLecturer(LecturerRequest request) {
        if (personRepository.existsById(request.getId())
                || systemAdminUser.equals(request.getId())) {
            throw new AppException(ErrorCode.USER_EXISTED, "Mã giảng viên đã tồn tại hoặc trùng với admin hệ thống");
        }

        Lecturer lecturer = lecturerMapper.toEntity(request);

        // Xử lý gán danh sách bộ môn
        setSubDepartments(lecturer, request.getSubDepartmentIds());

        return LecturerResponse.fromEntity(lecturerRepository.save(lecturer));
    }

    @Transactional
    public LecturerResponse updateLecturer(String id, LecturerRequest request) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy giảng viên để cập nhật"));

        lecturerMapper.updateLecturer(lecturer, request);

        // Cập nhật lại danh sách bộ môn
        setSubDepartments(lecturer, request.getSubDepartmentIds());

        return LecturerResponse.fromEntity(lecturerRepository.save(lecturer));
    }

    private void setSubDepartments(Lecturer lecturer, Set<String> subDepartmentIds) {
        if (subDepartmentIds != null && !subDepartmentIds.isEmpty()) {
            List<SubDepartment> subDepartments = subDepartmentRepository.findAllById(subDepartmentIds);
            if (subDepartments.size() != subDepartmentIds.size()) {
                throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Một hoặc nhiều bộ môn không tồn tại");
            }
            lecturer.setSubDepartments(new HashSet<>(subDepartments));
        }
    }

    @Transactional
    public void deleteLecturer(String id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy giảng viên để xóa"));

        if (accountRepository.existsByPerson(lecturer)) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                    "Không thể xóa giảng viên này vì đã được cấp tài khoản hệ thống.");
        }

        lecturerRepository.delete(lecturer);
    }
}