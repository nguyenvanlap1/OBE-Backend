package com.OBE.workflow.service;

import com.OBE.workflow.repository.AccountRepository;
import com.OBE.workflow.repository.LecturerRepository;
import com.OBE.workflow.dto.request.CreateLecturerRequest;
import com.OBE.workflow.entity.Account;
import com.OBE.workflow.entity.Lecturer;
import com.OBE.workflow.exception.AppException;
import com.OBE.workflow.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${obe.system-admin.username}")
    private String systemAdminUser;

    @Transactional
    public Lecturer createLecturer(CreateLecturerRequest request) {
        // 1. Kiểm tra tồn tại
        if (accountRepository.existsByUsername(request.getId())
                || systemAdminUser.equals(request.getId())) {
            throw new AppException(ErrorCode.USER_EXISTED, "Mã người dùng đã tồn tại");
        }

        // 2. Tạo Account
        Account account = Account.builder()
                .username(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // 3. Tạo Lecturer và gắn Account vào (Quan hệ OneToOne)
        Lecturer lecturer = Lecturer.builder()
                .account(account)
                .fullName(request.getFullName())
                .gender(request.getGender())
                .build();
        return lecturerRepository.save(lecturer);
    }
}