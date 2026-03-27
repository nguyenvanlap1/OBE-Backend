-- Sử dụng INSERT IGNORE để tránh lỗi nếu chạy lại script
INSERT IGNORE INTO khoa_bo_mon (ma_bo_mon, ten_bo_mon, ma_khoa, mieu_ta_khac) VALUES
-- Thuộc Khoa Công nghệ Thông tin (FIT)
('FIT_IS', 'Hệ thống thông tin', 'FIT', 'Chuyên về cơ sở dữ liệu và phân tích hệ thống'),
('FIT_SE', 'Công nghệ phần mềm', 'FIT', 'Đào tạo quy trình phát triển phần mềm chuyên nghiệp'),
('FIT_NET', 'Mạng máy tính và Truyền thông', 'FIT', 'Quản trị mạng và an ninh mạng'),
('FIT_AI', 'Khoa học máy tính & AI', 'FIT', 'Nghiên cứu trí tuệ nhân tạo và học máy'),

-- Thuộc Khoa Cơ khí (FME)
('FME_AUTO', 'Công nghệ Kỹ thuật Ô tô', 'FME', 'Nghiên cứu động cơ và hệ thống điều khiển ô tô'),
('FME_MEC', 'Cơ điện tử', 'FME', 'Sự kết hợp giữa cơ khí và điện tử điều khiển'),

-- Thuộc Khoa Kinh tế (FE)
('FE_ACC', 'Kế toán - Kiểm toán', 'FE', 'Đào tạo nghiệp vụ kế toán doanh nghiệp'),
('FE_BA', 'Quản trị kinh doanh', 'FE', 'Đào tạo kỹ năng quản lý và khởi nghiệp'),
('FE_MKT', 'Marketing', 'FE', 'Nghiên cứu thị trường và hành vi người tiêu dùng'),

-- Thuộc Khoa Ngoại ngữ (FL)
('FL_ENG', 'Ngôn ngữ Anh', 'FL', 'Tiếng Anh thương mại và biên phiên dịch'),
('FL_JPN', 'Ngôn ngữ Nhật', 'FL', 'Tiếng Nhật giao tiếp và văn hóa doanh nghiệp'),

-- Thuộc Khoa Kỹ thuật Xây dựng (FCE)
('FCE_CIV', 'Kỹ thuật Xây dựng Dân dụng', 'FCE', 'Thiết kế kết cấu công trình dân dụng'),
('FCE_ROAD', 'Xây dựng Cầu đường', 'FCE', 'Thiết kế hệ thống hạ tầng giao thông'),

-- Thuộc Khoa Khoa học Cơ bản (FAS)
('FAS_MATH', 'Toán học', 'FAS', 'Giảng dạy toán giải tích và toán ứng dụng'),
('FAS_PHY', 'Vật lý lý thuyết', 'FAS', 'Nghiên cứu các nguyên lý vật lý cơ bản'),

-- Thuộc Khoa Nông nghiệp (FAG)
('FAG_CROP', 'Khoa học cây trồng', 'FAG', 'Nghiên cứu giống cây và kỹ thuật canh tác'),

-- Thuộc Khoa Sư phạm (FSE)
('FSE_MATH', 'Sư phạm Toán', 'FSE', 'Đào tạo giáo viên toán cấp THPT'),
('FSE_LIT', 'Sư phạm Ngữ văn', 'FSE', 'Đào tạo giáo viên ngữ văn cấp THPT');