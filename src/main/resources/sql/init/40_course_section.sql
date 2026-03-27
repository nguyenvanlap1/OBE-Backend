-- =============================================
-- 9. THÊM LỚP HỌC PHẦN (CourseSection)
-- =============================================

INSERT IGNORE INTO lop_hoc_phan (ma_lop_hoc_phan, semester_id, ma_hoc_phan, so_thu_tu_phien_ban, ma_giang_vien) VALUES
-- --- HỌC KỲ 1 (2024-2025) ---
-- Môn Java nâng cao (IT101)
('LHP_JAVA_241_01', 1, 'IT101', 1, 'GV001'), -- Thầy A dạy
('LHP_JAVA_241_02', 2, 'IT101', 1, 'GV002'), -- Cô B dạy

-- Môn OOAD (IT202)
('LHP_OOAD_241_01', 3, 'IT202', 1, 'GV001'), -- Thầy A dạy lớp này luôn
('LHP_OOAD_241_02', 1, 'IT202', 1, 'GV004'), -- Thầy D dạy

-- --- HỌC KỲ 2 (2024-2025) ---
-- Mở lại môn Java cho sinh viên học lại/học vượt
('LHP_JAVA_242_01', 2, 'IT101', 1, 'GV001'),

-- --- HỌC KỲ 1 (2025-2026) ---
-- Các lớp mới cho niên khóa tiếp theo
('LHP_JAVA_251_01', 3, 'IT101', 1, 'GV002'),
('LHP_OOAD_251_01', 1, 'IT202', 1, 'GV004');