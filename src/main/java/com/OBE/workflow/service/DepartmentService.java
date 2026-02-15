package com.OBE.workflow.service;

import com.OBE.workflow.dto.request.DepartmentFilterRequest;
import com.OBE.workflow.repository.DepartmentRepository;
import com.OBE.workflow.dto.request.DepartmentRequest;
import com.OBE.workflow.entity.Department;
import com.OBE.workflow.exception.AppException;
import com.OBE.workflow.exception.ErrorCode;
import com.OBE.workflow.repository.SubDepartmentRepository;
import com.OBE.workflow.repository.specification.DepartmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;
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

    public Page<Department> getDepartments(Pageable pageable, DepartmentFilterRequest departmentFilterRequest) {
        Specification<Department> spec = Specification
                .where(DepartmentSpecification.hasName(departmentFilterRequest.getName()))
                .and(DepartmentSpecification.hasId(departmentFilterRequest.getId()));
        return departmentRepository.findAll(spec, pageable);
    }

    public void deleteDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy khoa hoặc trường để xóa"));

        // Sử dụng existsBy để tối ưu hiệu năng
        if (subDepartmentRepository.existsByDepartment(department)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Không thể xóa: Khoa vẫn còn các bộ môn trực thuộc");
        }

        departmentRepository.deleteById(id);
    }
}
