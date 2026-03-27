package com.OBE.workflow.feature.course_version;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course.Course;
import com.OBE.workflow.feature.course.CourseRepository;
import com.OBE.workflow.feature.course_version.assessment.Assessment;
import com.OBE.workflow.feature.course_version.assessment.AssessmentRepository;
import com.OBE.workflow.feature.course_version.clo.CLO;
import com.OBE.workflow.feature.course_version.clo.CLORepository;
import com.OBE.workflow.feature.course_version.co.CO;
import com.OBE.workflow.feature.course_version.co.CORepository;
import com.OBE.workflow.feature.course_version.mapping.AssessmentCloMapping;
import com.OBE.workflow.feature.course_version.mapping.AssessmentCloMappingRepository;
import com.OBE.workflow.feature.course_version.mapping.CoCloMapping;
import com.OBE.workflow.feature.course_version.mapping.CoCloMappingRepository;
import com.OBE.workflow.feature.course_version.request.CourseVersionFilterRequest;
import com.OBE.workflow.feature.course_version.request.CourseVersionRequestCreateDetail;
import com.OBE.workflow.feature.course_version.request.CourseVersionRequestCreateFirstDetail;
import com.OBE.workflow.feature.course_version.request.CourseVersionRequestUpdateDetail;
import com.OBE.workflow.feature.course_version.response.CourseVersionResponse;
import com.OBE.workflow.feature.course_version.response.CourseVersionResponseDetail;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import com.OBE.workflow.util.DebugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseVersionService {

    private static final Logger log = LoggerFactory.getLogger(CourseVersionService.class);
    private final CourseVersionRepository courseVersionRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final CourseRepository courseRepository;
    private final CoCloMappingRepository coCloMappingRepository;
    private final AssessmentCloMappingRepository assessmentCloMappingRepository;
    private final CORepository coRepository;
    private final CLORepository cloRepository;
    private final AssessmentRepository assessmentRepository;

    @Transactional(readOnly = true)
    public Page<CourseVersionResponse> getCourseVersions(Pageable pageable, CourseVersionFilterRequest filter) {
        Specification<CourseVersion> spec = Specification
                .where(CourseVersionSpecification.hasCourseId(filter.getCourseId()))
                .and(CourseVersionSpecification.hasVersionNumber(filter.getVersionNumber()))
                .and(CourseVersionSpecification.hasCredits(filter.getCredits()))
                .and(CourseVersionSpecification.hasCourseName(filter.getCourseName()))
                .and(Boolean.TRUE.equals(filter.getActive()) ? CourseVersionSpecification.isActive() : null)
                .and(CourseVersionSpecification.hasSubDepartmentId(filter.getSubDepartmentId()))
                .and(CourseVersionSpecification.hasDepartmentId(filter.getDepartmentId()))
                .and(CourseVersionSpecification.hasEducationProgramId(filter.getEducationProgramId()))
                .and(CourseVersionSpecification.isLatestVersion());

        return courseVersionRepository.findAll(spec, pageable)
                .map(CourseVersionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CourseVersionResponseDetail getCourseVersionDetail(String courseId, Integer versionNumber) {
        CourseVersion entity;

        if (versionNumber == null) {
            // Trường hợp version null: Lấy phiên bản mới nhất (Max Version)
            entity = courseVersionRepository.findFirstByCourseIdOrderByVersionNumberDesc(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản nào cho học phần: " + courseId));
        } else {
            // Trường hợp có versionNumber: Lấy đúng phiên bản đó (Dùng ID hỗn hợp)
            CourseVersionId id = new CourseVersionId(courseId, versionNumber);
            entity = courseVersionRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,"Không tìm thấy học phần bản: " + courseId + " - V" + versionNumber));
        }
        return CourseVersionResponseDetail.fromEntity(entity);
    }

    @Transactional
    public CourseVersionResponseDetail createFirstCourseVersionDetail(CourseVersionRequestCreateFirstDetail request) {
        if (courseRepository.existsById(request.getCourseId())) {
            throw(new AppException(ErrorCode.ENTITY_EXISTED, "Học phần đã tồn tại với mã: "+ request.getCourseId()));
        }
        // 2. Tìm SubDepartment (Bộ môn quản lý)
        SubDepartment subDept = subDepartmentRepository.findById(request.getSubDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bộ môn"));

        Course course = Course.builder()
                .id(request.getCourseId())
                .subDepartment(subDept)
                .build();
        course = courseRepository.save(course);

        CourseVersion version = CourseVersion.builder()
                .course(course)
                .name(request.getName())
                .versionNumber(1) // Mặc định là phiên bản đầu tiên
                .name(request.getName())
                .credits(request.getCredits())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .build();

        List<CO> cos = request.getCos().stream()
                .map(req -> CO.builder()
                        .coCode(req.getCoCode())
                        .content(req.getContent())
                        .courseVersion(version)
                        .build())
                .toList();
        version.setCos(cos);

        // 6. Map CLOs
        List<CLO> clos = request.getClos().stream()
                .map(req -> CLO.builder()
                        .cloCode(req.getCloCode())
                        .content(req.getContent())
                        .courseVersion(version)
                        .build())
                .toList();
        version.setClos(clos);

        List<Assessment> assessments = request.getAssessments().stream()
                .map(req -> Assessment.builder()
                        .assessmentCode(req.getAssessmentCode())
                        .name(req.getName())
                        .regulation(req.getRegulation())
                        .weight(req.getWeight())
                        .courseVersion(version)
                        .build())
                .toList();
        version.setAssessments(assessments);

        // 8. Lưu toàn bộ (nhờ CascadeType.ALL trong Entity CourseVersion)
        CourseVersion savedVersion = courseVersionRepository.save(version);

        System.out.println("print mapping at create fist: ");
        DebugUtils.logDeep(request);

        // 6. XỬ LÝ MAPPING CO - CLO
        for(var req: request.getCoCloMappings()) {
            CO targetCo = savedVersion.getCos().stream()
                    .filter(c -> c.getCoCode().equals(req.getCoCode())).findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CO không khớp: " + req.getCoCode()));
            CLO targetClo = savedVersion.getClos().stream()
                    .filter(c -> c.getCloCode().equals(req.getCloCode())).findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CLO không khớp: " + req.getCloCode()));

            CoCloMapping coCloMapping = CoCloMapping.builder().co(targetCo).clo(targetClo).weight(req.getWeight()).build();

            targetCo.getCoCloMappings().add(coCloMapping);
            targetClo.getCoCloMappings().add(coCloMapping);
        }

        for(var req: request.getAssessmentCloMappings()) {
            Assessment targetAs = savedVersion.getAssessments().stream()
                    .filter(a -> a.getAssessmentCode().equals(req.getAssessmentCode())).findFirst() // Dùng name theo request của bạn
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã Assessment không khớp: " + req.getAssessmentCode()));
            CLO targetClo = savedVersion.getClos().stream()
                    .filter(c -> c.getCloCode().equals(req.getCloCode())).findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CLO không khớp: " + req.getCloCode()));

            AssessmentCloMapping assessmentCloMapping = AssessmentCloMapping.builder()
                    .assessment(targetAs)
                    .clo(targetClo)
                    .weight(req.getWeight())
                    .build();

            targetAs.getAssessmentCloMappings().add(assessmentCloMapping);
            targetClo.getAssessmentCloMappings().add(assessmentCloMapping);
        }

        return CourseVersionResponseDetail.fromEntity(savedVersion);
    }

    @Transactional
    public CourseVersionResponseDetail createNextCourseVersionDetail(CourseVersionRequestCreateDetail request) {
        // 1. Kiểm tra Course gốc có tồn tại không
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học phần không tồn tại: " + request.getCourseId()));

        // 2. Tìm version lớn nhất hiện tại để cộng thêm 1
        Integer latestVersion = courseVersionRepository.findFirstByCourseIdOrderByVersionNumberDesc(request.getCourseId())
                .map(CourseVersion::getVersionNumber)
                .orElse(0);

        // 3. Khởi tạo CourseVersion mới
        CourseVersion nextVersion = CourseVersion.builder()
                .course(course)
                .versionNumber(latestVersion + 1)
                .name(request.getName())
                .credits(request.getCredits())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .build();

        // 4. Map CO, CLO, Assessment (Gán version vào từng object)
        nextVersion.setCos(request.getCos().stream()
                .map(req -> CO.builder().coCode(req.getCoCode()).content(req.getContent()).courseVersion(nextVersion).build())
                .toList());

        nextVersion.setClos(request.getClos().stream()
                .map(req -> CLO.builder().cloCode(req.getCloCode()).content(req.getContent()).courseVersion(nextVersion).build())
                .toList());

        nextVersion.setAssessments(request.getAssessments().stream()
                .map(req -> Assessment.builder().assessmentCode(req.getAssessmentCode()).name(req.getName())
                        .regulation(req.getRegulation()).weight(req.getWeight()).courseVersion(nextVersion).build())
                .toList());

        // 5. LƯU LẦN 1: Để sinh ID cho CO, CLO, Assessment
        CourseVersion savedVersion = courseVersionRepository.save(nextVersion);

        // 6. XỬ LÝ MAPPING CO - CLO
        List<CoCloMapping> coCloMappings = request.getCoCloMappings().stream()
                .map(req -> {
                    CO targetCo = savedVersion.getCos().stream()
                            .filter(c -> c.getCoCode().equals(req.getCoCode())).findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CO không khớp: " + req.getCoCode()));
                    CLO targetClo = savedVersion.getClos().stream()
                            .filter(c -> c.getCloCode().equals(req.getCloCode())).findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CLO không khớp: " + req.getCloCode()));

                    return CoCloMapping.builder().co(targetCo).clo(targetClo).weight(req.getWeight()).build();
                }).toList();
        coCloMappingRepository.saveAll(coCloMappings);

        // 7. XỬ LÝ MAPPING ASSESSMENT - CLO
        List<AssessmentCloMapping> asCloMappings = request.getAssessmentCloMappings().stream()
                .map(req -> {
                    Assessment targetAs = savedVersion.getAssessments().stream()
                            .filter(a -> a.getAssessmentCode().equals(req.getAssessmentCode())).findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã Assessment không khớp: " + req.getAssessmentCode()));
                    CLO targetClo = savedVersion.getClos().stream()
                            .filter(c -> c.getCloCode().equals(req.getCloCode())).findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Mã CLO không khớp: " + req.getCloCode()));

                    return AssessmentCloMapping.builder().assessment(targetAs).clo(targetClo).weight(req.getWeight()).build();
                }).toList();
        assessmentCloMappingRepository.saveAll(asCloMappings);

        return CourseVersionResponseDetail.fromEntity(savedVersion);
    }

    @Transactional
    public CourseVersionResponseDetail updateCourseVersionDetail(CourseVersionRequestUpdateDetail request) {
        // 1. Tìm phiên bản hiện tại (Dùng khóa hỗn hợp CourseId + VersionNumber)
        CourseVersionId id = new CourseVersionId(request.getCourseId(), request.getVersionNumber());
        CourseVersion version = courseVersionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần để cập nhật"));

        // 2. Cập nhật thông tin cơ bản của Version
        version.setName(request.getName());
        version.setCredits(request.getCredits());
        version.setFromDate(request.getFromDate());
        version.setToDate(request.getToDate());

        List<CO> cos = version.getCos();
        // --- KHU VỰC XỬ LÝ CO ---

        // 1. Xác định danh sách ID từ request và danh sách cần xóa
        Set<Long> reqCoIds = request.getCos().stream()
                .map(CourseVersionRequestUpdateDetail.CoRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CO> cosToDelete = version.getCos().stream()
                .filter(co -> !reqCoIds.contains(co.getId()))
                .collect(Collectors.toList());

        // 2. Thực hiện xóa triệt để CO cũ và Mapping liên quan
        if (!cosToDelete.isEmpty()) {
            List<Long> dCoIds = cosToDelete.stream().map(CO::getId).toList();

            // Xóa mapping liên quan đến CO sắp bị xóa
            coCloMappingRepository.deleteByCoIds(dCoIds);
            coCloMappingRepository.flush();

            // Gỡ khỏi list cha và xóa trong DB
            version.getCos().removeAll(cosToDelete);
            coRepository.deleteAllInBatch(cosToDelete);
            coRepository.flush();
        }

        // 3. Gán mã tạm cho các CO đang update để tránh lỗi Unique Constraint (Swap mã)
        long coTempSuffix = System.currentTimeMillis();
        for (var coReq : request.getCos()) {
            if (coReq.getId() != null) {
                CO existing = version.getCos().stream()
                        .filter(c -> c.getId().equals(coReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "ID CO không hợp lệ"));

                existing.setCoCode(existing.getCoCode() + "_tmp_" + coTempSuffix);
            }
        }
        coRepository.flush(); // Đẩy mã tạm xuống DB

        // 4. Cập nhật chính thức hoặc Thêm mới CO
        for (var coReq : request.getCos()) {
            if (coReq.getId() != null) {
                // Cập nhật CO đã tồn tại (Lúc này mã gốc đã trống chỗ)
                CO existing = version.getCos().stream()
                        .filter(c -> c.getId().equals(coReq.getId()))
                        .findFirst().get();

                existing.setCoCode(coReq.getCoCode());
                existing.setContent(coReq.getContent());
            } else {
                // Thêm mới hoàn toàn
                // Kiểm tra trùng mã trong bộ nhớ (trường hợp user gửi 2 CO mới trùng code)
                boolean isCodeDuplicate = version.getCos().stream()
                        .anyMatch(c -> c.getCoCode().equalsIgnoreCase(coReq.getCoCode()));

                if (isCodeDuplicate) {
                    CO existingByCode = version.getCos().stream()
                            .filter(c -> c.getCoCode().equalsIgnoreCase(coReq.getCoCode()))
                            .findFirst().get();
                    existingByCode.setContent(coReq.getContent());
                } else {
                    CO newCo = CO.builder()
                            .coCode(coReq.getCoCode())
                            .content(coReq.getContent())
                            .courseVersion(version)
                            .build();
                    version.getCos().add(newCo);
                }
            }
        }
// Kết thúc khu vực CO, tiếp tục đến khu vực CLO...

        List<CLO> clos = version.getClos();

        // Lấy danh sách ID hợp lệ (lọc null luôn)
        // 1. Lấy danh sách ID từ request (loại bỏ null)
        Set<Long> requestIds = request.getClos().stream()
                .map(CourseVersionRequestUpdateDetail.CloRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 2. Xác định các CLO cần xóa (có trong DB nhưng không có trong Request ID)
        // 2. Xác định các CLO cần xóa
        List<CLO> toDelete = clos.stream()
                .filter(clo -> !requestIds.contains(clo.getId()))
                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            for (CLO clo : toDelete) {
                // BƯỚC A: Gỡ bỏ liên kết ở mức Object (Quan trọng nhất để tránh lỗi Transaction)
                // Việc này báo cho Hibernate biết: "Đừng quan tâm đến Object này nữa"
                clos.remove(clo);
                clo.setCourseVersion(null);
            }

            // BƯỚC B: Xóa dữ liệu ở các bảng Mapping trước (Foreign Key)
            // Lưu ý: Dùng ID để xóa trực tiếp bằng Query sẽ an toàn hơn
            List<Long> deleteIds = toDelete.stream().map(CLO::getId).toList();
            assessmentCloMappingRepository.deleteByCloIds(deleteIds);
            coCloMappingRepository.deleteByCloIds(deleteIds);

            // BƯỚC C: Ép Hibernate đẩy lệnh xóa Mapping xuống DB ngay
            assessmentCloMappingRepository.flush();
            coCloMappingRepository.flush();

            // BƯỚC D: Xóa CLO bằng Repository (Dùng InBatch để dứt khoát)
            cloRepository.deleteAllInBatch(toDelete);

            // BƯỚC E: Flush lần cuối để DB sạch sẽ trước khi vào vòng lặp Update/Insert
            cloRepository.flush();
        }

        for (var cloReq : request.getClos()) {
            if (cloReq.getId() != null) {
                CLO existing = clos.stream()
                        .filter(c -> c.getId().equals(cloReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "ID không hợp lệ"));

                // Đặt mã tạm thời: CLO1 -> CLO1_tmp_123456
                existing.setCloCode(existing.getCloCode() + "_tmp_" + System.currentTimeMillis());
            }
        }

        // Ép DB cập nhật các mã tạm này để giải phóng hoàn toàn các mã gốc (CLO1, CLO2...)
        cloRepository.flush();

        // 4. Duyệt qua request để xử lý
        for (var cloReq : request.getClos()) {
            if (cloReq.getId() != null) {
                // TRƯỜNG HỢP UPDATE: Có ID cụ thể
                CLO existing = clos.stream()
                        .filter(clo -> clo.getId().equals(cloReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "ID không hợp lệ"));

                existing.setCloCode(cloReq.getCloCode());
                existing.setContent(cloReq.getContent());
            } else {
                // TRƯỜNG HỢP THÊM MỚI (ID == null)
                // Kiểm tra xem trong list hiện tại (đã xóa) có cái nào trùng Code không để tránh lỗi Unique Constraint
                boolean isCodeDuplicate = clos.stream()
                        .anyMatch(c -> c.getCloCode().equalsIgnoreCase(cloReq.getCloCode()));

                if (isCodeDuplicate) {
                    // Nếu trùng mã với một cái đang tồn tại, báo lỗi hoặc cập nhật cái đó luôn
                    CLO existingByCode = clos.stream()
                            .filter(c -> c.getCloCode().equalsIgnoreCase(cloReq.getCloCode()))
                            .findFirst().get();
                    existingByCode.setContent(cloReq.getContent());
                } else {
                    // INSERT mới hoàn toàn
                    CLO newClo = CLO.builder()
                            .cloCode(cloReq.getCloCode())
                            .content(cloReq.getContent())
                            .courseVersion(version)
                            .build();
                    clos.add(newClo);
                }
            }
        }

        List<Assessment> assessments = version.getAssessments();
        // --- KHU VỰC XỬ LÝ ASSESSMENT ---

        // --- BƯỚC 1: TIỀN XỬ LÝ THÔNG MINH ---
        // Tạo một Set để theo dõi các ID đã được gán/sử dụng trong request
        Set<Long> assignedIds = new HashSet<>();

        // Bước tiền xử lý
        for (var aReq : request.getAssessments()) {
            if (aReq.getId() != null) {
                assignedIds.add(aReq.getId()); // Đánh dấu các ID đã có sẵn từ Frontend
            }
        }

        for (var aReq : request.getAssessments()) {
            if (aReq.getId() == null) {
                version.getAssessments().stream()
                        .filter(a -> a.getAssessmentCode().equalsIgnoreCase(aReq.getAssessmentCode()))
                        // QUAN TRỌNG: Chỉ chọn ID chưa bị thằng nào trong request chiếm mất
                        .filter(a -> !assignedIds.contains(a.getId()))
                        .findFirst()
                        .ifPresent(existing -> {
                            aReq.setId(existing.getId());
                            assignedIds.add(existing.getId()); // Đánh dấu đã dùng ID này để cứu
                        });
            }
        }

        // 1. Xác định danh sách ID từ request và danh sách cần xóa
        Set<Long> reqAsmIds = request.getAssessments().stream()
                .map(CourseVersionRequestUpdateDetail.AssessmentRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Assessment> asmsToDelete = version.getAssessments().stream()
                .filter(a -> !reqAsmIds.contains(a.getId()))
                .collect(Collectors.toList());

        // 2. Thực hiện xóa triệt để Assessment cũ và Mapping liên quan
        if (!asmsToDelete.isEmpty()) {
            List<Long> dAsmIds = asmsToDelete.stream().map(Assessment::getId).toList();

            // Xóa mapping liên quan (Foreign Key)
            assessmentCloMappingRepository.deleteByAsmIds(dAsmIds);
            assessmentCloMappingRepository.flush();

            // Gỡ khỏi list cha và xóa trong DB
            version.getAssessments().removeAll(asmsToDelete);
            assessmentRepository.deleteAllInBatch(asmsToDelete);
            assessmentRepository.flush();
        }

        // 3. Gán mã tạm để tránh lỗi Unique khi Swap mã Assessment
        long asmTempSuffix = System.currentTimeMillis();
        for (var aReq : request.getAssessments()) {
            if (aReq.getId() != null) {
                Assessment existing = version.getAssessments().stream()
                        .filter(a -> a.getId().equals(aReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "ID Assessment không hợp lệ"));

                existing.setAssessmentCode(existing.getAssessmentCode() + "_tmp_" + asmTempSuffix);
            }
        }
        assessmentRepository.flush(); // Đẩy mã tạm xuống DB

        // 4. Cập nhật chính thức hoặc Thêm mới Assessment
        for (var aReq : request.getAssessments()) {
            if (aReq.getId() != null) {
                // Cập nhật Assessment đã tồn tại
                Assessment existing = version.getAssessments().stream()
                        .filter(a -> a.getId().equals(aReq.getId()))
                        .findFirst().get();

                existing.setAssessmentCode(aReq.getAssessmentCode());
                existing.setName(aReq.getName());
                existing.setRegulation(aReq.getRegulation());
                existing.setWeight(aReq.getWeight());
            } else {
                // Thêm mới hoàn toàn
                // Kiểm tra trùng mã trong bộ nhớ (trường hợp user gửi 2 Assessment mới trùng code)
                boolean isCodeDuplicate = version.getAssessments().stream()
                        .anyMatch(a -> a.getAssessmentCode().equalsIgnoreCase(aReq.getAssessmentCode()));

                if (isCodeDuplicate) {
                    Assessment existingByCode = version.getAssessments().stream()
                            .filter(a -> a.getAssessmentCode().equalsIgnoreCase(aReq.getAssessmentCode()))
                            .findFirst().get();
                    existingByCode.setName(aReq.getName());
                    existingByCode.setRegulation(aReq.getRegulation());
                    existingByCode.setWeight(aReq.getWeight());
                } else {
                    Assessment newAsm = Assessment.builder()
                            .assessmentCode(aReq.getAssessmentCode())
                            .courseVersion(version)
                            .name(aReq.getName())
                            .regulation(aReq.getRegulation())
                            .weight(aReq.getWeight())
                            .build();
                    version.getAssessments().add(newAsm);
                }
            }
        }

        // 3. XỬ LÝ DANH SÁCH CON (CO, CLO, Assessment)
        // Xóa sạch các Mapping cũ trước khi thay đổi CO/CLO/Assessment để tránh lỗi Constraint
        coCloMappingRepository.deleteByCourseVersion(version);
        assessmentCloMappingRepository.deleteByCourseVersion(version);
        coCloMappingRepository.flush(); // Đảm bảo lệnh xóa thực thi ngay
        assessmentCloMappingRepository.flush();

        // Bước 2: QUAN TRỌNG - Xóa trắng list mapping trong từng Entity Java
        cos.forEach(co -> co.getCoCloMappings().clear());
        clos.forEach(clo -> clo.getCoCloMappings().clear());
        clos.forEach(clo -> clo.getAssessmentCloMappings().clear());
        assessments.forEach(a -> a.getAssessmentCloMappings().clear());

        long count = assessmentCloMappingRepository.countByCourseVersion(version);
        System.out.println(">>> SAU DELETE còn: " + count);

        Map<String, CO> coMap = cos.stream().collect(Collectors.toMap(CO::getCoCode, c -> c));
        Map<String, CLO> cloMap = clos.stream().collect(Collectors.toMap(CLO::getCloCode, c -> c));
        Map<String, Assessment> assessmentMap = assessments.stream().collect(Collectors.toMap(Assessment::getAssessmentCode, a -> a));

        DebugUtils.logDeep(request.getCoCloMappings());
        DebugUtils.logDeep(request.getAssessmentCloMappings());

        for(var coCloMappingRequest : request.getCoCloMappings()) {
            CO co = coMap.get(coCloMappingRequest.getCoCode());
            CLO clo = cloMap.get(coCloMappingRequest.getCloCode());

            if(co == null || clo == null)
                continue;

            CoCloMapping coCloMapping = CoCloMapping.builder()
                    .clo(clo)
                    .co(co)
                    .weight(coCloMappingRequest.getWeight()).build();

            co.getCoCloMappings().add(coCloMapping);
            clo.getCoCloMappings().add(coCloMapping);
        }

        for (var mappingReq : request.getAssessmentCloMappings()) {
            Assessment assessment = assessmentMap.get(mappingReq.getAssessmentCode());
            CLO clo = cloMap.get(mappingReq.getCloCode());

            // Nếu Assessment hoặc CLO không tồn tại, bỏ qua
            if (assessment == null || clo == null) continue;

            // Tạo mapping mới
            AssessmentCloMapping mapping = AssessmentCloMapping.builder()
                    .assessment(assessment)
                    .clo(clo)
                    .weight(mappingReq.getWeight())
                    .build();

            // Gắn mapping vào cả Assessment và CLO
            assessment.getAssessmentCloMappings().add(mapping);
            clo.getAssessmentCloMappings().add(mapping);
        }

        System.out.println("========== KIỂM TRA PHÂN TÍCH KHÓA NGOẠI (FOREIGN KEYS) ==========");

        // 1. Soát lỗi Duplicate uq_co_clo (co_id, clo_id)
        System.out.println(">>> [BẢNG co_clo_mapping]");
        for (CLO clo : version.getClos()) {
            if (clo.getCoCloMappings() != null) {
                for (CoCloMapping m : clo.getCoCloMappings()) {
                    System.out.printf("    MAPPING_ID: %-5s | CO_ID: %-4d (Code: %-5s) | CLO_ID: %-4d (Code: %-5s) | Weight: %s%n",
                            m.getId() == null ? "NEW" : m.getId(),
                            m.getCo().getId(),
                            m.getCo().getCoCode(),
                            m.getClo().getId(),
                            m.getClo().getCloCode(),
                            m.getWeight());
                }
            }
        }

        System.out.println("------------------------------------------------------------------");

        // 2. Soát lỗi Duplicate uq_assessment_clo (assessment_id, clo_id) -> ĐÂY LÀ CHỖ LỖI 7-7
        System.out.println(">>> [BẢNG assessment_clo_mapping]");
        for (Assessment a : version.getAssessments()) {
            if (a.getAssessmentCloMappings() != null) {
                for (AssessmentCloMapping m : a.getAssessmentCloMappings()) {
                    System.out.printf("    MAPPING_ID: %-5s | ASSESS_ID: %-4d (Code: %-5s) | CLO_ID: %-4d (Code: %-5s) | Weight: %s%n",
                            m.getId() == null ? "NEW" : m.getId(),
                            m.getAssessment().getId(),
                            m.getAssessment().getAssessmentCode(),
                            m.getClo().getId(),
                            m.getClo().getCloCode(),
                            m.getWeight());
                }
            }
        }
        System.out.println("========== KẾT THÚC KIỂM TRA KHÓA NGOẠI ==========");

        return CourseVersionResponseDetail.fromEntity(version);
    }

    @Transactional
    public void deleteCourseVersion(String courseId, Integer versionNumber) {
        // 1. Tìm Entity (Dùng FindById để JPA quản lý đối tượng này trong Persistence Context)
        CourseVersionId id = new CourseVersionId(courseId, versionNumber);
        CourseVersion version = courseVersionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy phiên bản " + versionNumber + " của học phần " + courseId));
        // 2. Thực hiện xóa
        // Nhờ hệ thống Cascade bạn đã thiết lập:
        // CourseVersion -> CLO -> CoCloMapping & AssessmentCloMapping (Tự động xóa sạch)
        courseVersionRepository.delete(version);
        // 3. Logic dọn dẹp Course (Tùy chọn)
        // Nếu xóa phiên bản này xong mà Course không còn phiên bản nào khác, xóa luôn Course
        if (!courseVersionRepository.existsByCourseIdAndVersionNumberNot(courseId, versionNumber)) {
            courseRepository.deleteById(courseId);
        }
    }
}