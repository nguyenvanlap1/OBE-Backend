-- Giả sử thêm trạm trung chuyển cho lớp Java (LHP_JAVA_241_01)
-- Lớp này dùng IT101 phiên bản 1, có Assessment ID là 1 và 2
-- Thêm lại với đúng tên cột so_thu_tu_cot_diem
INSERT INTO cham_trung_chuyen_diem (so_thu_tu_cot_diem, course_section_id, assessment_id) VALUES
(1, 'LHP_JAVA_241_01', 1), -- Bài tập về nhà
(2, 'LHP_JAVA_241_01', 2); -- Thi cuối kỳ

-- Cho lớp OOAD (LHP_OOAD_241_01) - IT202 phiên bản 1, Assessment ID là 3 và 4
INSERT IGNORE INTO cham_trung_chuyen_diem (so_thu_tu_cot_diem, course_section_id, assessment_id) VALUES
(1, 'LHP_OOAD_241_01', 3), -- Đồ án giữa kỳ
(2, 'LHP_OOAD_241_01', 4); -- Thi vấn đáp

-- Đăng ký sinh viên vào lớp học phần (LHP_JAVA_241_01)
-- ma_sinh_vien | ma_lop_hoc_phan
INSERT IGNORE INTO dang_ky_hoc_phan (ma_sinh_vien, ma_lop_hoc_phan) VALUES
('B2003501', 'LHP_JAVA_241_01'), -- Nguyễn Văn An
('B2003502', 'LHP_JAVA_241_01'), -- Trần Thị Bình
('B2003503', 'LHP_JAVA_241_01'); -- Lê Hoàng Chúc

-- Vào điểm cho Nguyễn Văn An (B2003501) tại lớp LHP_JAVA_241_01
INSERT IGNORE INTO diem_so (enrollment_id, section_assessment_id, diem_so)
VALUES (
    (SELECT id FROM dang_ky_hoc_phan WHERE ma_sinh_vien = 'B2003501' AND ma_lop_hoc_phan = 'LHP_JAVA_241_01'),
    (SELECT id FROM cham_trung_chuyen_diem WHERE course_section_id = 'LHP_JAVA_241_01' AND so_thu_tu_cot_diem = 1),
    8.5 -- Điểm bài tập
),
(
    (SELECT id FROM dang_ky_hoc_phan WHERE ma_sinh_vien = 'B2003501' AND ma_lop_hoc_phan = 'LHP_JAVA_241_01'),
    (SELECT id FROM cham_trung_chuyen_diem WHERE course_section_id = 'LHP_JAVA_241_01' AND so_thu_tu_cot_diem = 2),
    7.0 -- Điểm thi
);

-- Tương tự cho Trần Thị Bình (B2003502)
INSERT IGNORE INTO diem_so (enrollment_id, section_assessment_id, diem_so)
VALUES (
    (SELECT id FROM dang_ky_hoc_phan WHERE ma_sinh_vien = 'B2003502' AND ma_lop_hoc_phan = 'LHP_JAVA_241_01'),
    (SELECT id FROM cham_trung_chuyen_diem WHERE course_section_id = 'LHP_JAVA_241_01' AND so_thu_tu_cot_diem = 1),
    9.0
),
(
    (SELECT id FROM dang_ky_hoc_phan WHERE ma_sinh_vien = 'B2003502' AND ma_lop_hoc_phan = 'LHP_JAVA_241_01'),
    (SELECT id FROM cham_trung_chuyen_diem WHERE course_section_id = 'LHP_JAVA_241_01' AND so_thu_tu_cot_diem = 2),
    8.5
);