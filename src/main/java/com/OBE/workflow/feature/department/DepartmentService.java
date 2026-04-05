package com.OBE.workflow.feature.department;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.department.response.DepartmentSummaryResponse;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import com.OBE.workflow.feature.department.request.DepartmentFilterRequest;
import com.OBE.workflow.feature.department.request.DepartmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional(readOnly = true)
    public Page<Department> getDepartments(Pageable pageable, DepartmentFilterRequest departmentFilterRequest) {
        Specification<Department> spec = Specification
                .where(DepartmentSpecification.hasName(departmentFilterRequest.getName()))
                .and(DepartmentSpecification.hasId(departmentFilterRequest.getId()));
        return departmentRepository.findAll(spec, pageable);
    }

    @Transactional
    public Department createDepartment(DepartmentRequest departmentRequest) {
        if(departmentRepository.existsById(departmentRequest.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Khoa đã tồn tại");
        }
        Department department = Department.builder()
                .id(departmentRequest.getId())
                .name(departmentRequest.getName())
                .description(departmentRequest.getDescription()).build();
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(String id, DepartmentRequest departmentRequest) {
        // 1. Kiểm tra xem Department có tồn tại không
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy khoa hoặc trường để cập nhật"));

        // 2. Cập nhật các trường thông tin (tránh cập nhật ID vì ID là khóa chính)
        department.setName(departmentRequest.getName());
        department.setDescription(departmentRequest.getDescription());

        /// 2. MapStruct tự động map tất cả các trường từ request vào department
        departmentMapper.updateDepartment(department, departmentRequest);

        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy khoa hoặc trường để xóa"));

        // Sử dụng existsBy để tối ưu hiệu năng
        if (subDepartmentRepository.existsByDepartment(department)) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Không thể xóa: Khoa vẫn còn các bộ môn trực thuộc");
        }
        departmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<DepartmentSummaryResponse> getAllSummaries() {
        return departmentRepository.findAll().stream()
                .map(DepartmentSummaryResponse::fromEntity)
                .toList();
    }
}
