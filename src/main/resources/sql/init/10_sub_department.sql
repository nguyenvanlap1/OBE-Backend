INSERT IGNORE INTO hoc_phan (ma_hoc_phan, ten_hoc_phan, ma_bo_mon) VALUES
('IT0201', 'Lập trình hướng đối tượng', 'IT02'),
('IT0202', 'Cấu trúc dữ liệu và Giải thuật', 'IT02'),
('IT0101', 'Cơ sở dữ liệu', 'IT01'),
('EC0201', 'Quản trị học đại cương', 'EC02'),
('LN0101', 'Tiếng Anh chuyên ngành 1', 'LN01');

INSERT INTO hoc_phan_phien_ban (ma_hoc_phan, so_thu_tu_phien_ban, so_tin_chi, ap_dung_tu_ngay, ap_dung_den_ngay, ten_hoc_phan) VALUES
-- Học phần IT0201
('IT0201', 1, 3, '2023-09-01', '2024-08-31', 'Lập trình hướng đối tượng (Legacy)'),
('IT0201', 2, 3, '2024-09-01', NULL,         'Lập trình hướng đối tượng'),

-- Học phần IT0101
('IT0101', 1, 4, '2024-01-01', NULL,         'Cơ sở dữ liệu nâng cao'),

-- Học phần EC0201
('EC0201', 1, 3, '2023-09-01', '2025-01-01', 'Quản trị học'),
('EC0201', 2, 2, '2025-01-02', NULL,         'Quản trị học hiện đại');

-- Dữ liệu CO (Mục tiêu học phần) cho IT0201 v2
INSERT INTO co (ma_co, noi_dung_muc_tieu, ma_hoc_phan, so_thu_tu_phien_ban) VALUES
('CO1', 'Hiểu các nguyên lý cơ bản của OOP như đóng gói, kế thừa.', 'IT0201', 2),
('CO2', 'Có khả năng thiết kế hệ thống phần mềm nhỏ bằng Java.', 'IT0201', 2);

-- Dữ liệu CLO (Chuẩn đầu ra học phần) cho IT0201 v2
INSERT INTO clo (ma_clo, noi_dung_chuan_dau_ra, ma_hoc_phan, so_thu_tu_phien_ban) VALUES
('CLO1.1', 'Giải thích được khái niệm Class và Object.', 'IT0201', 2),
('CLO1.2', 'Sử dụng thành thạo tính đa hình trong lập trình.', 'IT0201', 2),
('CLO2.1', 'Xây dựng được ứng dụng quản lý có kết nối DB.', 'IT0201', 2);

INSERT INTO diem_thanh_phan (ten_thanh_phan, quy_dinh, trong_so, ma_hoc_phan, so_thu_tu_phien_ban, ma_danh_gia) VALUES
('Chuyên cần', 'Tham gia trên 80% số tiết', 0.1, 'IT0201', 2, 'CC'),
('Kiểm tra giữa kỳ', 'Làm bài trên máy 60 phút', 0.3, 'IT0201', 2, 'GK'),
('Đồ án cuối kỳ', 'Xây dựng phần mềm hoàn chỉnh', 0.6, 'IT0201', 2, 'CK');
