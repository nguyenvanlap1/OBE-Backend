package com.OBE.workflow.controller;

import com.OBE.workflow.repository.LecturerRepository;
import com.OBE.workflow.dto.request.CreateLecturerRequest;
import com.OBE.workflow.dto.response.ApiResponse;
import com.OBE.workflow.entity.Lecturer;
import com.OBE.workflow.service.LecturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;
    private final LecturerRepository lecturerRepository; // Vẫn giữ để Get All nhanh

    @GetMapping
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createLecturer(@Valid @RequestBody CreateLecturerRequest request) {
        Lecturer savedLecturer = lecturerService.createLecturer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Đã tạo giảng viên")
                .data(Map.of(
                        "id", savedLecturer.getAccount().getUsername(),
                        "fullName", savedLecturer.getFullName(),
                        "gender", savedLecturer.getGender()
                ))
                .build());
    }
}