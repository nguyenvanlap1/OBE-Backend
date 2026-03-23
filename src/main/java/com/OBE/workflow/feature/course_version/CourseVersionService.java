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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseVersionService {

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
                            .filter(a -> a.getAssessmentCode().equals(req.getAssessmentCode())).findFirst() // Dùng name theo request của bạn
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

        // 3. XỬ LÝ DANH SÁCH CON (CO, CLO, Assessment)
        // Xóa sạch các Mapping cũ trước khi thay đổi CO/CLO/Assessment để tránh lỗi Constraint
        coCloMappingRepository.deleteByCourseVersion(version);
        assessmentCloMappingRepository.deleteByCourseVersion(version);

        List<CO> cos = version.getCos();
        List<Long> cosIds = request.getCos().stream().map(CourseVersionRequestUpdateDetail.CoRequest::getId).toList();
        cos.removeIf(co -> !cosIds.contains(co.getId()));

        for (var coReq : request.getCos()) {
            if (coReq.getId() != null) {
                // Cập nhật CO đã tồn tại
                CO existing = cos.stream()
                        .filter(co -> co.getId().equals(coReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                                "CO với id " + coReq.getId() + " không thuộc phiên bản này"));
                existing.setCoCode(coReq.getCoCode());
                existing.setContent(coReq.getContent());
            } else {
                CO newCo = CO.builder()
                        .coCode(coReq.getCoCode())
                        .content(coReq.getContent())
                        .courseVersion(version)
                        .build();
                cos.add(newCo);
            }
        }

        List<CLO> clos = version.getClos();
        List<Long> cloIds = request.getClos().stream()
                .map(CourseVersionRequestUpdateDetail.CloRequest::getId)
                .toList();

        // Xóa những CLO không có trong request
        clos.removeIf(clo -> !cloIds.contains(clo.getId()));

        // Duyệt request để cập nhật hoặc thêm mới
        for (var cloReq : request.getClos()) {
            if (cloReq.getId() != null) {
                // Cập nhật CLO đã tồn tại
                CLO existing = clos.stream()
                        .filter(clo -> clo.getId().equals(cloReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                                "CLO với id " + cloReq.getId() + " không thuộc phiên bản này"));
                existing.setCloCode(cloReq.getCloCode());
                existing.setContent(cloReq.getContent());
            } else {
                // Thêm mới CLO
                CLO newClo = CLO.builder()
                        .cloCode(cloReq.getCloCode())
                        .content(cloReq.getContent())
                        .courseVersion(version)
                        .build();
                clos.add(newClo);
            }
        }

        List<Assessment> assessments = version.getAssessments();
        List<Long> assessmentIds = request.getAssessments().stream()
                .map(CourseVersionRequestUpdateDetail.AssessmentRequest::getId)
                .toList();

        // Xóa những Assessment không có trong request
        assessments.removeIf(a -> !assessmentIds.contains(a.getId()));

        // Duyệt request để cập nhật hoặc thêm mới
        for (var aReq : request.getAssessments()) {
            if (aReq.getId() != null) {
                // Cập nhật Assessment đã tồn tại
                Assessment existing = assessments.stream()
                        .filter(a -> a.getId().equals(aReq.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                                "Assessment với id " + aReq.getId() + " không thuộc phiên bản này"));
                existing.setAssessmentCode(aReq.getAssessmentCode());
                existing.setName(aReq.getName());
                existing.setRegulation(aReq.getRegulation());
                existing.setWeight(aReq.getWeight()); // ví dụ nếu có trường weight
            } else {
                // Thêm mới Assessment
                Assessment newAssessment = Assessment.builder()
                        .assessmentCode(aReq.getAssessmentCode())
                        .courseVersion(version)
                        .name(aReq.getName())
                        .regulation(aReq.getRegulation())
                        .weight(aReq.getWeight())
                        .build();
                assessments.add(newAssessment);
            }
        }

        Map<String, CO> coMap = cos.stream().collect(Collectors.toMap(CO::getCoCode, c -> c));
        Map<String, CLO> cloMap = clos.stream().collect(Collectors.toMap(CLO::getCloCode, c -> c));
        Map<String, Assessment> assessmentMap = assessments.stream().collect(Collectors.toMap(Assessment::getAssessmentCode, a -> a));

        DebugUtils.logDeep(request.getCoCloMappings());

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