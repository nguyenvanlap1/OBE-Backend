package com.OBE.workflow.feature.officer;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.officer.request.OfficerRequest;
import com.OBE.workflow.conmon.authorization.account.AccountRepository;
import com.OBE.workflow.conmon.authorization.account.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final OfficerMapper officerMapper; // Tiêm Mapper vào đây

    @Value("${obe.system-admin.username}")
    private String systemAdminUser;

    // --- Hàm tìm kiếm và Phân trang (Giữ nguyên logic Specification) ---
    @Transactional(readOnly = true)
    public Page<Officer> getOfficers(Pageable pageable, OfficerRequest filter) {
        Specification<Officer> spec = Specification
                .where(OfficerSpecification.hasId(filter.getId()))
                .and(OfficerSpecification.hasFullName(filter.getFullName()))
                .and(OfficerSpecification.hasGender(filter.getGender()));
        return officerRepository.findAll(spec, pageable);
    }

    // --- Tạo mới Officer ---
    @Transactional
    public Officer createOfficer(OfficerRequest request) {
        // 1. Kiểm tra tồn tại
        if (personRepository.existsById(request.getId())
                || systemAdminUser.equals(request.getId())) {
            throw new AppException(ErrorCode.USER_EXISTED, "Mã cán bộ đã tồn tại hoặc trùng với admin hệ thống");
        }

        // 2. Sử dụng Mapper để chuyển từ Request sang Entity
        Officer officer = officerMapper.toEntity(request);

        return officerRepository.save(officer);
    }

    // --- Cập nhật Officer ---
    @Transactional
    public Officer updateOfficer(String id, OfficerRequest request) {
        Officer officer = officerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy cán bộ để cập nhật"));

        // Sử dụng Mapper để cập nhật dữ liệu từ request vào entity hiện tại
        officerMapper.updateOfficer(officer, request);

        return officerRepository.save(officer);
    }

    // --- Xóa Officer ---
    @Transactional
    public void deleteOfficer(String id) {
        Officer officer = officerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy cán bộ để xóa"));

        if (accountRepository.existsByPerson(officer)) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                    "Không thể xóa cán bộ này vì đã được cấp tài khoản hệ thống. Hãy xóa tài khoản trước.");
        }

        officerRepository.delete(officer);
    }
}