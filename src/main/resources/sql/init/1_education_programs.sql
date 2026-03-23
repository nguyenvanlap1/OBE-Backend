-- ================================
-- 1. CHƯƠNG TRÌNH ĐÀO TẠO (CTDT)
-- ================================
INSERT IGNORE INTO chuong_trinh_dao_tao
(ma_chuong_trinh_dao_tao, ten_chuong_trinh_dao_tao, trinh_do_dao_tao, so_tin_chi_yeu_cau, ma_bo_mon)
VALUES
('CTDT_CNPM_2024', 'Công nghệ Phần mềm', 'Đại học chính quy', 145, 'IT02'),
('CTDT_HTTT_2024', 'Hệ thống Thông tin', 'Đại học chính quy', 142, 'IT01'),
('CTDT_KHMT_2024', 'Khoa học Máy tính', 'Đại học chính quy', 140, 'IT04');

-- ================================
-- 2. PO
-- ================================
INSERT IGNORE INTO muc_tieu_dao_tao
(ma_po, noi_dung, ma_chuong_trinh)
VALUES
('PO1', 'Có kiến thức nền tảng vững chắc về toán học và khoa học tự nhiên.', 'CTDT_CNPM_2024'),
('PO2', 'Khả năng thiết kế, phát triển và bảo trì các hệ thống phần mềm phức tạp.', 'CTDT_CNPM_2024'),
('PO3', 'Kỹ năng làm việc nhóm, giao tiếp và đạo đức nghề nghiệp tốt.', 'CTDT_CNPM_2024');

-- ================================
-- 3. PLO
-- ================================
INSERT IGNORE INTO chuan_dau_ra_chuong_trinh
(ma_plo, noi_dung, ma_chuong_trinh)
VALUES
('PLO1', 'Vận dụng được kiến thức cơ bản về lập trình hướng đối tượng vào thực tế.', 'CTDT_CNPM_2024'),
('PLO2', 'Sử dụng thành thạo các công cụ quản lý mã nguồn như Git.', 'CTDT_CNPM_2024'),
('PLO3', 'Có khả năng đọc hiểu tài liệu chuyên ngành bằng tiếng Anh.', 'CTDT_CNPM_2024');

-- ================================
-- 4. CTDT - NIÊN KHÓA
-- ================================
INSERT IGNORE INTO chuong_trinh_dao_tao_nien_khoa
(ma_chuong_trinh_dao_tao, khoa)
VALUES
('CTDT_CNPM_2024', 'K46'),
('CTDT_CNPM_2024', 'K47'),
('CTDT_HTTT_2024', 'K46'),
('CTDT_KHMT_2024', 'K48');

-- ================================
-- 5. PLO - PO MAPPING (QUAN TRỌNG)
-- ================================

-- PLO1 -> PO1
INSERT IGNORE INTO plo_po_mapping (ma_plo, ma_po, weight) -- Liệt kê rõ các cột, bỏ qua cột 'id'
SELECT p.id, po.id, 1.0
FROM chuan_dau_ra_chuong_trinh p
JOIN muc_tieu_dao_tao po
  ON p.ma_chuong_trinh = po.ma_chuong_trinh
WHERE p.ma_plo = 'PLO1' AND po.ma_po = 'PO1';

-- PLO2 -> PO2
INSERT IGNORE INTO plo_po_mapping (ma_plo, ma_po, weight)
SELECT p.id, po.id, 1.0
FROM chuan_dau_ra_chuong_trinh p
JOIN muc_tieu_dao_tao po
  ON p.ma_chuong_trinh = po.ma_chuong_trinh
WHERE p.ma_plo = 'PLO2' AND po.ma_po = 'PO2';

-- PLO3 -> PO3
INSERT IGNORE INTO plo_po_mapping (ma_plo, ma_po, weight)
SELECT p.id, po.id, 1.0
FROM chuan_dau_ra_chuong_trinh p
JOIN muc_tieu_dao_tao po
  ON p.ma_chuong_trinh = po.ma_chuong_trinh
WHERE p.ma_plo = 'PLO3' AND po.ma_po = 'PO3';