package com.OBE.workflow.feature.sup_department;

import com.OBE.workflow.feature.education_program.EducationProgramRepository;
import com.OBE.workflow.feature.sup_department.request.SubDepartmentFilterRequest;
import com.OBE.workflow.feature.sup_department.request.SubDepartmentRequest;
import com.OBE.workflow.feature.department.Department;
import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.department.DepartmentRepository;
import com.OBE.workflow.permission.repository.AccountRoleSubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubDepartmentService {
    private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final EducationProgramRepository educationProgramRepository;
    private final AccountRoleSubDepartmentRepository accountRoleSubDepartmentRepository;
    private final SubDepartmentMapper subDepartmentMapper;

    // --- Hàm bổ sung: Tìm kiếm và Phân trang ---
    @Transactional(readOnly = true)
    public Page<SubDepartment> getSubDepartments(Pageable pageable, SubDepartmentFilterRequest filter) {
        Specification<SubDepartment> spec = Specification
                .where(SubDepartmentSpecification.hasName(filter.getName()))
                .and(SubDepartmentSpecification.hasId(filter.getId()))
                .and(SubDepartmentSpecification.hasDepartmentId(filter.getDepartmentId()))
                .and(SubDepartmentSpecification.hasDepartmentName(filter.getDepartmentName())); // Lọc theo khoa

        return subDepartmentRepository.findAll(spec, pageable);
    }

    @Transactional
    public SubDepartment createSubDepartment(SubDepartmentRequest request) {
        if (subDepartmentRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Mã bộ môn đã tồn tại");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy khoa để gán bộ môn"));

        SubDepartment subDepartment = subDepartmentMapper.toEntity(request);
        subDepartment.setDepartment(department);

        return subDepartmentRepository.save(subDepartment);
    }

    @Transactional
    public SubDepartment updateSubDepartment(String id, SubDepartmentRequest request) {
        SubDepartment subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bộ môn để cập nhật"));

        if (!subDepartment.getDepartment().getId().equals(request.getDepartmentId())) {
            Department newDepartment = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy khoa mới để cập nhật"));
            subDepartment.setDepartment(newDepartment);
        }

        subDepartmentMapper.updateSubDepartment(subDepartment, request);
        return subDepartmentRepository.save(subDepartment);
    }

    @Transactional
    public void deleteSubDepartment(String id) {
        SubDepartment subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy khoa hoặc bộ môn để xóa"));
        if(educationProgramRepository.existsBySubDepartment(subDepartment))
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Không thể xóa khoa hoặc bộ môn do vẫn còn các chương trình đào tạo trực thuộc");
        if(accountRoleSubDepartmentRepository.existsBySubDepartment(subDepartment))
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Không thể xóa khoa hoặc bộ môn do vẫn còn các các bộ trực thuộc");
        subDepartmentRepository.deleteById(id);
    }
}