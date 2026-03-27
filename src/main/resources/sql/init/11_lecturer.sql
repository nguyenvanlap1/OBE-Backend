-- 1. Thêm vào bảng Person (Giả định bảng tên là 'ca_nhan')
INSERT IGNORE INTO ca_nhan (ma_ca_nhan, ho_ten, gioi_tinh) VALUES
('GV001', 'Nguyễn Văn A', 'Nam'),
('GV002', 'Trần Thị B', 'Nữ'),
('GV003', 'Lê Hoàng C', 'Nam'),
('GV004', 'Phạm Minh D', 'Nam'),
('GV005', 'Đặng Mỹ E', 'Nữ');

-- 2. Thêm vào bảng Lecturer (giang_vien)
INSERT IGNORE INTO giang_vien (ma_ca_nhan) VALUES
('GV001'), ('GV002'), ('GV003'), ('GV004'), ('GV005');

-- 3. Gán Giảng viên vào Bộ môn (nhan_su_bo_mon)
INSERT IGNORE INTO nhan_su_bo_mon (ma_giang_vien, ma_bo_mon) VALUES
('GV001', 'FIT_SE'),  -- Nguyễn Văn A thuộc Công nghệ phần mềm
('GV001', 'FIT_AI'),  -- Nguyễn Văn A kiêm nhiệm Khoa học máy tính & AI
('GV002', 'FIT_IS'),  -- Trần Thị B thuộc Hệ thống thông tin
('GV003', 'FME_AUTO'), -- Lê Hoàng C thuộc Kỹ thuật Ô tô
('GV004', 'FE_BA'),    -- Phạm Minh D thuộc Quản trị kinh doanh
('GV005', 'FL_JPN');   -- Đặng Mỹ E thuộc Ngôn ngữ Nhật