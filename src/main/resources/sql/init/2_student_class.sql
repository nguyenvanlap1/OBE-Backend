-- =============================================
-- 6. LỚP SINH VIÊN (StudentClass)
-- Logic: Mỗi lớp thuộc 1 Niên khóa và 1 CTĐT cụ thể
-- =============================================

INSERT IGNORE INTO lop_sinh_vien
(ma_lop, ten_lop, khoa, ma_chuong_trinh_dao_tao)
VALUES
-- Các lớp thuộc ngành Công nghệ Phần mềm (K46, K47)
('DI2096A1', 'Công nghệ phần mềm 1 - Khóa 46', 'K46', 'CTDT_CNPM_2024'),
('DI2096A2', 'Công nghệ phần mềm 2 - Khóa 46', 'K46', 'CTDT_CNPM_2024'),
('DI2196A1', 'Công nghệ phần mềm 1 - Khóa 47', 'K47', 'CTDT_CNPM_2024'),

-- Các lớp thuộc ngành Hệ thống Thông tin (K46)
('DI2095A1', 'Hệ thống thông tin 1 - Khóa 46', 'K46', 'CTDT_HTTT_2024'),
('DI2095A2', 'Hệ thống thông tin 2 - Khóa 46', 'K46', 'CTDT_HTTT_2024'),

-- Lớp thuộc ngành Khoa học Máy tính (K48)
('DI2294A1', 'Khoa học máy tính 1 - Khóa 48', 'K48', 'CTDT_KHMT_2024');