INSERT IGNORE INTO hoc_phan (ma_hoc_phan, ma_bo_mon) VALUES
('IT101', 'FIT_SE'),
('IT202', 'FIT_SE');

-- =============================================
-- 3. THÊM PHIÊN BẢN HỌC PHẦN (CourseVersion)
-- =============================================
INSERT IGNORE INTO hoc_phan_phien_ban (ma_hoc_phan, so_thu_tu_phien_ban, so_tin_chi, ten_hoc_phan, ap_dung_tu_ngay) VALUES
('IT101', 1, 3, 'Lập trình Java nâng cao', '2024-01-01'),
('IT202', 1, 3, 'Phân tích thiết kế hướng đối tượng', '2024-01-01');

-- =============================================
-- 4. THÊM CHUẨN ĐẦU RA (CLO) - Lưu ý ID tự tăng nếu có
-- =============================================
INSERT IGNORE INTO clo (id, ma_clo, noi_dung_chuan_dau_ra, ma_hoc_phan, so_thu_tu_phien_ban) VALUES
(1, 'CLO1', 'Sử dụng thành thạo cấu trúc điều khiển trong Java', 'IT101', 1),
(2, 'CLO2', 'Xây dựng được ứng dụng Java Swing cơ bản', 'IT101', 1),
(3, 'CLO1', 'Vẽ được sơ đồ UML cho hệ thống', 'IT202', 1),
(4, 'CLO2', 'Thiết kế được cơ sở dữ liệu quan hệ từ sơ đồ lớp', 'IT202', 1),
(5, 'CLO3', 'Sử dụng công cụ CASE để vẽ biểu đồ hệ thống', 'IT202', 1);

-- =============================================
-- 5. THÊM MỤC TIÊU HỌC PHẦN (CO)
-- =============================================
INSERT IGNORE INTO co (id, ma_co, noi_dung_muc_tieu, ma_hoc_phan, so_thu_tu_phien_ban) VALUES
(1, 'CO1', 'Có khả năng lập trình hướng đối tượng', 'IT101', 1),
(2, 'CO1', 'Có khả năng phân tích yêu cầu phần mềm', 'IT202', 1);

-- =============================================
-- 6. THÊM ĐIỂM THÀNH PHẦN (Assessment)
-- =============================================
INSERT IGNORE INTO diem_thanh_phan (id, ten_thanh_phan, trong_so, ma_hoc_phan, so_thu_tu_phien_ban, quy_dinh) VALUES
(1, 'Bài tập về nhà', 0.2, 'IT101', 1, 'Bắt buộc'),
(2, 'Thi cuối kỳ', 0.8, 'IT101', 1, 'Bắt buộc'),
(3, 'Đồ án giữa kỳ', 0.3, 'IT202', 1, 'Bắt buộc'),
(4, 'Thi vấn đáp', 0.7, 'IT202', 1, 'Bắt buộc');

-- =============================================
-- 7. ÁNH XẠ CO - CLO (CoCloMapping)
-- =============================================
INSERT IGNORE INTO co_clo_mapping (co_id, clo_id, weight) VALUES
(1, 1, 0.5),
(1, 2, 0.5),
(2, 3, 0.4),
(2, 4, 0.6);

-- =============================================
-- 8. ÁNH XẠ ASSESSMENT - CLO (AssessmentCloMapping)
-- =============================================
INSERT IGNORE INTO assessment_clo_mapping (assessment_id, clo_id, weight) VALUES
(1, 1, 1.0),
(2, 2, 1.0),
(3, 3, 0.5),
(3, 5, 0.5),
(4, 4, 1.0);