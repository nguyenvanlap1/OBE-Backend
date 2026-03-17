package com.OBE.workflow.feature.course;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course.assessment_component.Assessment;
import com.OBE.workflow.feature.course.assessment_component.AssessmentCloMapping;
import com.OBE.workflow.feature.course.assessment_component.AssessmentCloMappingRepository;
import com.OBE.workflow.feature.course.clo.CLO;
import com.OBE.workflow.feature.course.clo.CoCloMapping;
import com.OBE.workflow.feature.course.clo.CoCloMappingRepository;
import com.OBE.workflow.feature.course.course_version.*;
import com.OBE.workflow.feature.course.request.CourseCreateRequest;
import com.OBE.workflow.feature.course.request.CourseFilterRequest;
import com.OBE.workflow.feature.course.request.CourseUpdateRequest;
import com.OBE.workflow.feature.course.request.CourseUpdateRequestDetail;
import com.OBE.workflow.feature.course.response.CourseResponse;
import com.OBE.workflow.feature.course.response.CourseResponseDetail;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import com.OBE.workflow.feature.course.co.CO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final CoCloMappingRepository coCloMappingRepository;
    private final AssessmentCloMappingRepository assessmentCloMappingRepository;
    private final CourseMapper courseMapper;
    private final CourseVersionMapper courseVersionMapper;

    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {

        // 1. Kiểm tra trùng mã học phần
        if (courseRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Học phần đã tồn tại");
        }

        // 2. Kiểm tra bộ môn tồn tại (rất quan trọng để tránh FK lỗi)
        if (!subDepartmentRepository.existsById(request.getSubDepartmentId())) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bộ môn quản lý");
        }

        // 3. Map sang Course (chỉ lưu thông tin gốc)
        Course course = courseMapper.toCourse(request);
        courseRepository.save(course);

        // 4. Tạo version đầu tiên (v1)
        CourseVersion initialVersion =
                courseVersionMapper.toInitialVersion(request, course);
        initialVersion.setCourse(course);

        courseVersionRepository.save(initialVersion);

        return courseMapper.toResponse(initialVersion.getCourse(), initialVersion);
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> getCourses(Pageable pageable, CourseFilterRequest filter) {
        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> {
                    if (order.getProperty().equals("id")) {
                        return new Sort.Order(order.getDirection(), "course.id");
                    }

                    if (order.getProperty().equals("subDepartmentId")) {
                        return new Sort.Order(order.getDirection(), "course.subDepartment.id");
                    }
                    return order;
                })
                .toList();

        // 2. Tạo đối tượng Sort mới và Pageable mới
        Sort correctedSort = Sort.by(orders);
        Pageable correctedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                correctedSort
        );

        Specification<CourseVersion> spec = Specification
                .where(CourseVersionSpecification.isActive())
                .and(CourseVersionSpecification.hasVersionNumber(filter.getVersionNumber()))
                .and(CourseVersionSpecification.hasCourseId(filter.getId()))
                .and(CourseVersionSpecification.hasName(filter.getName()))
                .and(CourseVersionSpecification.hasCredits(filter.getCredits()))
                .and(CourseVersionSpecification.hasDefaultName(filter.getDefaultName()))
                .and(CourseVersionSpecification.hasEducationProgramId((filter.getEducationProgramId())))
                .and(CourseVersionSpecification.hasSubDepartmentId(filter.getSubDepartmentId()))
                .and(CourseVersionSpecification.hasDepartmentId(filter.getDepartmentId()));

        Page<CourseVersion> versionPage = courseVersionRepository.findAll(spec, correctedPageable);
        return versionPage.map(version ->
                courseMapper.toResponse(version.getCourse(), version)
        );
    }

    @Transactional(readOnly = true)
    public CourseResponseDetail getCourseDetail(String courseId, Integer versionNumber) {
        // 1. Tìm thông tin học phần gốc
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học phần"));

        // 2. Tìm đúng phiên bản cần xem chi tiết
        CourseVersion version = courseVersionRepository
                .findByCourseIdAndVersionNumber(courseId, versionNumber)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                        "Không tìm thấy phiên bản " + versionNumber + " của học phần này"));

        // 3. Sử dụng Mapper để chuyển đổi thông tin cơ bản, danh sách CO, CLO, Assessments
        CourseResponseDetail detail = courseMapper.toDetailResponse(course, version);

        // 4. Lấy Mapping CO - CLO từ bảng trung gian (chứa weight)
        List<CourseResponseDetail.CoCloMappingResponse> coCloMappings = coCloMappingRepository
                .findByCourseVersion(version)
                .stream()
                .map(m -> CourseResponseDetail.CoCloMappingResponse.builder()
                        .coId(m.getCo().getId())
                        .cloId(m.getClo().getId())
                        .weight(m.getWeight())
                        .build())
                .toList();
        detail.setCoCloMappings(coCloMappings);

        // 5. Lấy Mapping Assessment - CLO từ bảng trung gian (chứa weight)
        List<CourseResponseDetail.AssessmentCloMappingResponse> assessmentCloMappings = assessmentCloMappingRepository
                .findByCourseVersion(version)
                .stream()
                .map(m -> CourseResponseDetail.AssessmentCloMappingResponse.builder()
                        .assessmentId(m.getAssessment().getId())
                        .cloId(m.getClo().getId())
                        .weight(m.getWeight())
                        .build())
                .toList();
        detail.setAssessmentCloMappings(assessmentCloMappings);

        return detail;
    }

    @Transactional
    public CourseResponse updateCourse(CourseUpdateRequest request) {
        // 1. Tìm Course gốc, nếu không thấy thì báo lỗi
        Course course = courseRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học phần"));

        // 2. Cập nhật thông tin chung của Course (tên mặc định, bộ môn)
        courseMapper.updateCourse(request, course);
        courseRepository.save(course);

        CourseVersion finalSelectedVersion;

        if (request.getIsNewVersion()) {
            // --- TRƯỜNG HỢP TẠO PHIÊN BẢN MỚI (v2, v3...) ---

            // Tìm số thứ tự phiên bản lớn nhất hiện tại để tăng lên 1
            Integer latestVersionNumber = courseVersionRepository.findMaxVersionByCourseId(course.getId());

            CourseVersion newVersion = courseVersionMapper.toNewVersion(request);
            newVersion.setCourse(course);
            newVersion.setVersionNumber(latestVersionNumber + 1);

            finalSelectedVersion = courseVersionRepository.save(newVersion);
        } else {
            // --- TRƯỜNG HỢP SỬA TRÊN PHIÊN BẢN HIỆN TẠI ---
            // Tìm đúng phiên bản cần sửa dựa trên courseId và versionNumber từ request
            CourseVersion existingVersion = courseVersionRepository
                    .findByCourseIdAndVersionNumber(course.getId(), request.getVersionNumber())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần cần cập nhật"));
            courseVersionMapper.updateVersion(request, existingVersion);
            finalSelectedVersion = courseVersionRepository.save(existingVersion);
        }

        // 3. Trả về kết quả đã map sang Response
        return courseMapper.toResponse(course, finalSelectedVersion);
    }

    @Transactional
    public void deleteCourseVersion(String courseId, Integer versionNumber) {
        // Kiểm tra phiên bản có tồn tại không
        CourseVersion version = courseVersionRepository
                .findByCourseIdAndVersionNumber(courseId, versionNumber)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần"));

        // Nếu là phiên bản duy nhất, có thể cân nhắc gọi hàm xóa toàn bộ Course hoặc báo lỗi
        long count = courseVersionRepository.countByCourseId(courseId);
        if (count <= 1) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Không thể xóa phiên bản duy nhất. Hãy dùng chức năng xóa học phần.");
        }

        courseVersionRepository.delete(version);
    }

    @Transactional
    public void deleteFullCourse(String courseId) {
        // 1. Tìm Course gốc
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học phần"));

        // 2. Kiểm tra xem Học phần này có còn "con" (phiên bản) nào không
        // Tương tự: if (subDepartmentRepository.existsByDepartment(department))
        if (courseVersionRepository.existsByCourseId(courseId)) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                    "Không thể xóa: Học phần vẫn còn các phiên bản trực thuộc. Vui lòng xóa hết phiên bản trước.");
        }

        // 3. Nếu không còn con, tiến hành xóa gốc
        courseRepository.delete(course);
    }
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllVersions(String courseId) {
        // 1. Kiểm tra Course có tồn tại không
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học phần"));

        // 2. Lấy tất cả version của học phần đó, sắp xếp theo số phiên bản mới nhất lên đầu
        List<CourseVersion> versions = courseVersionRepository.findByCourseIdOrderByVersionNumberDesc(courseId);

        // 3. Map danh sách Entity sang danh sách Response
        return versions.stream()
                .map(version -> courseMapper.toResponse(course, version))
                .toList();
    }

    @Transactional
    public CourseResponseDetail updateCourseVersionDetail(CourseUpdateRequestDetail request) {
        // 1. Tìm Version hiện tại (Composite Key)
        CourseVersionId versionId = new CourseVersionId(request.getCourseId(), request.getVersionNumber());
        CourseVersion version = courseVersionRepository.findById(versionId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần"));

        // 2. Cập nhật thông tin cơ bản
        version.setCredits(request.getCredits());
        version.setFromDate(request.getFromDate());
        version.setToDate(request.getToDate());

        // 3. Đối soát CO (Mục tiêu học phần) - Quan trọng để giữ liên kết PLO
        syncCos(version, request.getCos());

        // 4. Đối soát CLO (Chuẩn đầu ra)
        syncClos(version, request.getClos());

        // 5. Đối soát Assessments (Thành phần đánh giá)
        syncAssessments(version, request.getAssessments());

        // 6. Sau khi các entity đã ổn định, xử lý Mapping (Matrix)
        // Lưu ý: Phần Mapping Matrix thường xóa đi chèn lại vì nó là bảng trung gian đơn giản
        updateMappings(version, request);

        // Lưu vào database
        CourseVersion updatedVersion = courseVersionRepository.save(version);

        // Giả sử bạn lấy được Entity Course từ version
        Course course = updatedVersion.getCourse();

        // 7. Sử dụng Mapper để trả về object mong muốn
        return courseMapper.toDetailResponse(course, updatedVersion);
    }

    private void syncCos(CourseVersion version, List<CourseUpdateRequestDetail.CoRequest> requests) {
        // Xóa những CO không có trong request (Orphan Removal sẽ tự xử lý trong DB)
        version.getCos().removeIf(existing ->
                requests.stream().noneMatch(req -> req.getId() != null && req.getId().equals(existing.getId()))
        );

        for (var req : requests) {
            if (req.getId() != null) {
                // Update: Tìm entity đang được Hibernate quản lý để set giá trị mới
                version.getCos().stream()
                        .filter(co -> co.getId().equals(req.getId()))
                        .findFirst()
                        .ifPresent(co -> {
                            co.setCode(req.getCode());
                            co.setContent(req.getContent());
                        });
            } else {
                // Insert: Tạo mới và thiết lập quan hệ cha-con
                CO newCo = CO.builder()
                        .code(req.getCode())
                        .content(req.getContent())
                        .courseVersion(version)
                        .build();
                version.getCos().add(newCo);
            }
        }
    }

    private void syncClos(CourseVersion version, List<CourseUpdateRequestDetail.CloRequest> requests) {
        version.getClos().removeIf(existing ->
                requests.stream().noneMatch(req -> req.getId() != null && req.getId().equals(existing.getId()))
        );

        for (var req : requests) {
            if (req.getId() != null) {
                version.getClos().stream()
                        .filter(clo -> clo.getId().equals(req.getId()))
                        .findFirst()
                        .ifPresent(clo -> {
                            clo.setCode(req.getCode());
                            clo.setContent(req.getContent());
                        });
            } else {
                CLO newClo = CLO.builder()
                        .code(req.getCode())
                        .content(req.getContent())
                        .courseVersion(version)
                        .build();
                version.getClos().add(newClo);
            }
        }
    }

    private void syncAssessments(CourseVersion version, List<CourseUpdateRequestDetail.AssessmentRequest> requests) {
        version.getAssessments().removeIf(existing ->
                requests.stream().noneMatch(req -> req.getId() != null && req.getId().equals(existing.getId()))
        );

        for (var req : requests) {
            if (req.getId() != null) {
                version.getAssessments().stream()
                        .filter(a -> a.getId().equals(req.getId()))
                        .findFirst()
                        .ifPresent(a -> {
                            a.setName(req.getName());
                            a.setRegulation(req.getRegulation());
                            a.setWeight(req.getWeight());
                        });
            } else {
                Assessment newAs = Assessment.builder()
                        .name(req.getName())
                        .regulation(req.getRegulation())
                        .weight(req.getWeight())
                        .courseVersion(version)
                        .build();
                version.getAssessments().add(newAs);
            }
        }
    }

    private void updateMappings(CourseVersion version, CourseUpdateRequestDetail request) {

        // 1. XÓA mapping cũ
        coCloMappingRepository.deleteByCourseVersion(version);
        assessmentCloMappingRepository.deleteByCourseVersion(version);

        // 2. Map CO theo id để lookup nhanh
        Map<Long, CO> coMap = version.getCos().stream()
                .collect(Collectors.toMap(CO::getId, c -> c));

        Map<Long, CLO> cloMap = version.getClos().stream()
                .collect(Collectors.toMap(CLO::getId, c -> c));

        Map<Long, Assessment> assessmentMap = version.getAssessments().stream()
                .collect(Collectors.toMap(Assessment::getId, a -> a));

        // 3. Insert CO ↔ CLO mapping
        List<CoCloMapping> coCloMappings = request.getCoCloMappings().stream()
                .map(m -> CoCloMapping.builder()
                        .co(coMap.get(m.getCoId()))
                        .clo(cloMap.get(m.getCloId()))
                        .weight(m.getWeight())
                        .build())
                .toList();

        coCloMappingRepository.saveAll(coCloMappings);

        // 4. Insert Assessment ↔ CLO mapping
        List<AssessmentCloMapping> assessmentMappings = request.getAssessmentCloMappings().stream()
                .map(m -> AssessmentCloMapping.builder()
                        .assessment(assessmentMap.get(m.getAssessmentId()))
                        .clo(cloMap.get(m.getCloId()))
                        .weight(m.getWeight())
                        .build())
                .toList();

        assessmentCloMappingRepository.saveAll(assessmentMappings);
    }
}