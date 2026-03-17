-- Sử dụng INSERT IGNORE: Nếu trùng ma_bo_mon (Primary Key), MySQL sẽ bỏ qua dòng đó
-- Lưu ý: ma_khoa phải tồn tại trong bảng truong_khoa trước khi chạy script này

INSERT IGNORE INTO khoa_bo_mon (ma_bo_mon, ten_bo_mon, mieu_ta_khac, ma_khoa) VALUES
-- Các bộ môn thuộc Khoa Công nghệ Thông tin (FIT)
('IT01', 'Bộ môn Hệ thống thông tin', 'Phát triển và quản lý dữ liệu', 'FIT'),
('IT02', 'Bộ môn Công nghệ phần mềm', 'Quy trình và kỹ thuật lập trình', 'FIT'),
('IT03', 'Bộ môn Mạng máy tính', 'An ninh mạng và viễn thông', 'FIT'),
('IT04', 'Bộ môn Khoa học máy tính', 'Trí tuệ nhân tạo và thuật toán', 'FIT'),

-- Các bộ môn thuộc Khoa Kinh tế (FE)
('EC01', 'Bộ môn Kế toán', 'Kiểm toán và báo cáo tài chính', 'FE'),
('EC02', 'Bộ môn Quản trị kinh doanh', 'Lãnh đạo và khởi nghiệp', 'FE'),
('EC03', 'Bộ môn Tài chính ngân hàng', 'Thị trường chứng khoán và tiền tệ', 'FE'),

-- Các bộ môn thuộc Khoa Ngoại ngữ (FL)
('LN01', 'Bộ môn Tiếng Anh đại cương', 'Giảng dạy kỹ năng nghe nói đọc viết', 'FL'),
('LN02', 'Bộ môn Tiếng Nhật', 'Ngôn ngữ và văn hóa Nhật Bản', 'FL'),

-- Các bộ môn thuộc Khoa Cơ khí (FME)
('ME01', 'Bộ môn Cơ điện tử', 'Tự động hóa dây chuyền sản xuất', 'FME'),
('ME02', 'Bộ môn Công nghệ ô tô', 'Thiết kế và bảo trì động cơ', 'FME'),

-- Các bộ môn thuộc Khoa Sư phạm (FSE)
('ED01', 'Bộ môn Tâm lý giáo dục', 'Nghiên cứu hành vi người học', 'FSE'),
('ED02', 'Bộ môn Phương pháp giảng dạy', 'Kỹ thuật đứng lớp hiện đại', 'FSE'),

-- Các bộ môn thuộc Khoa Thiết kế Đồ họa (FAD)
('DE01', 'Bộ môn Thiết kế đồ họa 2D', 'Nhận diện thương hiệu và in ấn', 'FAD'),
('DE02', 'Bộ môn Diễn họa 3D', 'Thiết kế mô hình không gian 3 chiều', 'FAD');