-- =============================================
-- 1. THÔNG TIN CÁ NHÂN (ca_nhan)
-- Chiến lược JOINED: Cần có dữ liệu ở bảng cha trước
-- =============================================
INSERT IGNORE INTO ca_nhan (ma_ca_nhan, ho_ten, gioi_tinh) VALUES
('B2003501', 'Nguyễn Văn An', 'Nam'),
('B2003502', 'Trần Thị Bình', 'Nữ'),
('B2003503', 'Lê Hoàng Chúc', 'Nam'),
('B2104601', 'Phạm Minh Đăng', 'Nam'),
('B2104602', 'Võ Thị Hồng Đào', 'Nữ'),
('B2104603', 'Ngô Kiến Huy', 'Nam'),
('B2205701', 'Đặng Thu Thảo', 'Nữ'),
('B2205702', 'Bùi Anh Tuấn', 'Nam'),
('B2205703', 'Hoàng Thanh Trúc', 'Nữ'),
('B2001201', 'Lý Gia Thành', 'Nam'),
('B2001202', 'Nguyễn Mai Phương', 'Nữ'),
('B2001203', 'Trương Vĩnh Ký', 'Nam');

-- =============================================
-- 2. THÔNG TIN SINH VIÊN (sinh_vien)
-- Lưu ý: ma_ca_nhan đóng vai trò là Primary Key & Foreign Key
-- =============================================
INSERT IGNORE INTO sinh_vien (ma_ca_nhan) VALUES
('B2003501'), ('B2003502'), ('B2003503'),
('B2104601'), ('B2104602'), ('B2104603'),
('B2205701'), ('B2205702'), ('B2205703'),
('B2001201'), ('B2001202'), ('B2001203');

-- =============================================
-- 3. LIÊN KẾT LỚP - SINH VIÊN (lop_sinh_vien_chi_tiet)
-- Logic: mapping đúng tên bảng và tên cột trong @JoinTable của StudentClass
-- =============================================
INSERT IGNORE INTO lop_sinh_vien_chi_tiet (ma_lop, ma_sinh_vien) VALUES
-- Lớp Công nghệ phần mềm 1 - K46 (DI2096A1)
('DI2096A1', 'B2003501'),
('DI2096A1', 'B2003502'),

-- Lớp Công nghệ phần mềm 2 - K46 (DI2096A2)
('DI2096A2', 'B2003503'),

-- Lớp Công nghệ phần mềm 1 - K47 (DI2196A1)
('DI2196A1', 'B2104601'),
('DI2196A1', 'B2104602'),
('DI2196A1', 'B2104603'),

-- Lớp Hệ thống thông tin 1 - K46 (DI2095A1)
('DI2095A1', 'B2001201'),
('DI2095A1', 'B2001202'),

-- Lớp Hệ thống thông tin 2 - K46 (DI2095A2)
('DI2095A2', 'B2001203'),

-- Lớp Khoa học máy tính 1 - K48 (DI2294A1)
('DI2294A1', 'B2205701'),
('DI2294A1', 'B2205702'),
('DI2294A1', 'B2205703'),

-- Trường hợp sinh viên học song bằng (B2104601 học cả CNPM và HTTT)
('DI2095A2', 'B2104601');