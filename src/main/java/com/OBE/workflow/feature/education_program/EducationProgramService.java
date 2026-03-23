package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.CourseVersionRepository;
import com.OBE.workflow.feature.education_program.mapping.PloPoMapping;
import com.OBE.workflow.feature.education_program.mapping.PloPoMappingRepository;
import com.OBE.workflow.feature.education_program.plo.PLO;
import com.OBE.workflow.feature.education_program.plo.PLORepository;
import com.OBE.workflow.feature.education_program.po.PO;
import com.OBE.workflow.feature.education_program.po.PORepository;
import com.OBE.workflow.feature.education_program.request.EducationProgramFilterRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequestUpdateDetail;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponseDetail;
import com.OBE.workflow.feature.school_year.SchoolYear;
import com.OBE.workflow.feature.school_year.SchoolYearRepository;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationProgramService {

    private final EducationProgramRepository educationProgramRepository;
    private final EducationProgramMapper educationProgramMapper;
    private final SubDepartmentRepository subDepartmentRepository;
    private final SchoolYearRepository schoolYearRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final PloPoMappingRepository ploPoMappingRepository;
    private final PORepository poRepository;
    private final PLORepository ploRepository;

    @Transactional(readOnly = true)
    public Page<EducationProgramResponse> getEducationPrograms(Pageable pageable, EducationProgramFilterRequest filter) {
        // 1. Kết hợp các điều kiện lọc từ FilterRequest
        Specification<EducationProgram> spec = Specification
                .where(EducationProgramSpecification.hasId(filter.getId()))
                .and(EducationProgramSpecification.hasName(filter.getName()))
                .and(EducationProgramSpecification.hasEducationLevel(filter.getEducationLevel()))
                .and(EducationProgramSpecification.hasSubDepartmentId(filter.getSubDepartmentId()))
                .and(EducationProgramSpecification.hasDepartmentId(filter.getDepartmentId()))
                .and(EducationProgramSpecification.hasSchoolYear(filter.getSchoolYearId()));

        // 2. Truy vấn Database với phân trang và bộ lọc
        Page<EducationProgram> entityPage = educationProgramRepository.findAll(spec, pageable);

        // 3. Chuyển đổi sang Response (sử dụng Mapper để làm phẳng dữ liệu cho Frontend)
        return entityPage.map(educationProgramMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EducationProgramResponseDetail getEducationProgramDetail(String id) {
        // 1. Tìm chương trình đào tạo
        EducationProgram program = educationProgramRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo"));

        // 2. Tìm danh sách Mapping PLO-PO của chương trình này
        // Lưu ý: Bạn nên viết hàm này trong PloPoMappingRepository dựa trên mã chương trình
        List<PloPoMapping> mappings = ploPoMappingRepository.findAllByEducationProgram(program);

        // 3. Sử dụng Mapper để tổng hợp thành ResponseDetail
        EducationProgramResponseDetail educationProgramResponseDetail =  educationProgramMapper.toDetailResponse(program, mappings);
        educationProgramResponseDetail.setSchoolYearIds(program.getSchoolYears().stream().map(SchoolYear::getId).toList());
        return educationProgramMapper.toDetailResponse(program, mappings);
    }

    @Transactional
    public EducationProgram createEducationProgram(EducationProgramRequest request) {
        // 1. Kiểm tra trùng mã CTĐT
        if (educationProgramRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Chương trình đào tạo đã tồn tại");
        }

        // 2. Tìm Bộ môn (Bắt buộc)
        SubDepartment subDept = subDepartmentRepository.findById(request.getSubDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bộ môn"));

        // 3. Map các trường cơ bản sang Entity
        EducationProgram educationProgram = educationProgramMapper.toEntity(request);
        educationProgram.setSubDepartment(subDept);

        // 4. Xử lý danh sách Niên khóa (ManyToMany)
        if (request.getSchoolYearIds() != null) {
            List<SchoolYear> schoolYears = schoolYearRepository.findAllById(request.getSchoolYearIds());
            educationProgram.setSchoolYears(schoolYears);
        }
        return educationProgramRepository.save(educationProgram);
    }

    @Transactional
    public EducationProgramResponse updateEducationProgram(EducationProgramRequest request) {
        // 1. Tìm CTĐT hiện có
        EducationProgram educationProgram = educationProgramRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo"));

        // 2. Cập nhật các trường cơ bản bằng Mapper (@MappingTarget)
        educationProgramMapper.updateEntity(request, educationProgram);

        // 3. Cập nhật Bộ môn (nếu thay đổi)
        if (!educationProgram.getSubDepartment().getId().equals(request.getSubDepartmentId())) {
            SubDepartment subDept = subDepartmentRepository.findById(request.getSubDepartmentId())
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bộ môn mới"));
            educationProgram.setSubDepartment(subDept);
        }

        // 4. Cập nhật danh sách Niên khóa (Làm mới hoàn toàn danh sách)
        if (request.getSchoolYearIds() != null) {
            List<SchoolYear> schoolYears = schoolYearRepository.findAllById(request.getSchoolYearIds());
            educationProgram.setSchoolYears(schoolYears);
        }
        // 6. Lưu và trả về Response
        EducationProgram savedProgram = educationProgramRepository.save(educationProgram);
        return educationProgramMapper.toResponse(savedProgram);
    }

    @Transactional
    public void deleteEducationProgram(String id) {
        // 1. Kiểm tra sự tồn tại của chương trình
        EducationProgram program = educationProgramRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo để xóa"));

        // 2. Thực hiện xóa
        // JPA sẽ tự động DELETE các bản ghi liên quan trong bảng:
        // 'chuong_trinh_dao_tao_nien_khoa' và 'chi_tiet_chuong_trinh_dao_tao'
        educationProgramRepository.delete(program);
    }

    @Transactional
    public void addCourseToProgram(String programId, String courseId, Integer versionNumber) {
        // 1. Tìm chương trình đào tạo
        EducationProgram program = educationProgramRepository.findById(programId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo"));

        // 2. Xử lý chọn phiên bản học phần
        CourseVersion versionToAdd;
        if (versionNumber == null) {
            // Nếu không truyền version: Tìm phiên bản Active có số version cao nhất
            versionToAdd = courseVersionRepository.findActiveVersion(courseId)
                    .orElseGet(() -> courseVersionRepository.findFirstByCourseIdOrderByVersionNumberDesc(courseId)
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học phần này chưa có phiên bản nào")));
        } else {
            // Nếu có truyền version: Tìm đúng phiên bản đó
            versionToAdd = courseVersionRepository.findByCourseIdAndVersionNumber(courseId, versionNumber)
                    .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản học phần yêu cầu"));
        }

        // 3. Kiểm tra xem đã tồn tại trong danh sách chưa để tránh trùng lặp (Set-like behavior)
        if (program.getCourseVersions().contains(versionToAdd)) {
            throw new AppException(ErrorCode.ENTITY_EXISTED, "Học phần phiên bản này đã có trong chương trình");
        }

        // 4. Thêm vào danh sách và lưu
        program.getCourseVersions().add(versionToAdd);
        educationProgramRepository.save(program);
    }

    @Transactional
    public void removeCourseFromProgram(String programId, String courseId, Integer versionNumber) {
        EducationProgram program = educationProgramRepository.findById(programId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo"));

        // Tìm đúng đối tượng phiên bản trong list hiện tại để xóa
        CourseVersion versionToRemove = program.getCourseVersions().stream()
                .filter(v -> v.getCourse().getId().equals(courseId) && v.getVersionNumber().equals(versionNumber))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học phần này không tồn tại trong chương trình"));

        program.getCourseVersions().remove(versionToRemove);
        educationProgramRepository.save(program);
    }

    @Transactional
    public EducationProgramResponseDetail createProgramDetail(EducationProgramRequestUpdateDetail request) {
        if(educationProgramRepository.existsById(request.getId())) {
            throw (new AppException(ErrorCode.ENTITY_EXISTED, "Có 1 chương trình đào tạo đã tồn tại với mã: " + request.getId()));
        }
        EducationProgram educationProgram = new EducationProgram();
        educationProgram.setId(request.getId());

        SubDepartment subDepartment = subDepartmentRepository.findById(request.getSubDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy bộ môn nào có mã: " + request.getSubDepartmentId()));
        educationProgram.setSubDepartment(subDepartment);

        List<SchoolYear> schoolYears = schoolYearRepository.findAllById(request.getSchoolYearIds());
        if(schoolYears.isEmpty()) {
            throw (new AppException(ErrorCode.MISSING_REQUIRED_FIELD, "Niên khóa bị trống"));
        }
        educationProgram.setSchoolYears(schoolYears);

        educationProgramMapper.updateEntity(request, educationProgram);
        EducationProgram savedProgram = educationProgramRepository.save(educationProgram);
        List<PO> pos = updatePos(educationProgram, request.getPos());
        pos = poRepository.saveAll(pos); // ❗ Lưu các PO mới trước
        List<PLO> plos = updatePlos(educationProgram, request.getPlos());
        plos = ploRepository.saveAll(plos);
        List<PloPoMapping> ploPoMappings = updateMapping(savedProgram, request.getPloPoMappings(), pos, plos);
        return educationProgramMapper.toDetailResponse(savedProgram, ploPoMappings);
    }

    @Transactional
    public EducationProgramResponseDetail updateProgramDetail(String id, EducationProgramRequestUpdateDetail request) {
        // 1. Tìm entity cũ trong database
        EducationProgram program = educationProgramRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo với mã: " + id));

        // 2. Sử dụng Mapper để map dữ liệu từ request vào entity hiện tại
        // Lưu ý: Mapper của bạn đã ignore ID, PO, PLO, CourseVersions... nên chỉ update field cơ bản.
        educationProgramMapper.updateEntity(request, program);

        program.setSchoolYears(schoolYearRepository.findAllById(request.getSchoolYearIds()));
        // 1. Tìm SubDepartment mới từ Database
        SubDepartment subDepartment = subDepartmentRepository.findById(request.getSubDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));
        // Nếu AppException chỉ nhận 1 tham số ErrorCode
        // 2. Set vào entity
        program.setSubDepartment(subDepartment);

        // 3. Lưu tạm thời (với JPA @Transactional, việc save này sẽ update các field cơ bản trước)
        EducationProgram updatedProgram = educationProgramRepository.save(program);

        List<PO> newPos = updatePos(updatedProgram, request.getPos());
        List<PLO> newPlos = updatePlos(updatedProgram, request.getPlos());

        List<PloPoMapping> newMapping =  updateMapping(updatedProgram, request.getPloPoMappings(), newPos, newPlos);
        // 4. Trả về kết quả (Hiện tại List mappings truyền vào đang để trống vì chưa xử lý con)
        return educationProgramMapper.toDetailResponse(updatedProgram, newMapping);
    }

    private List<PO> updatePos(EducationProgram program, List<EducationProgramRequestUpdateDetail.PoRequest> poRequests) {
        // 1. Kiểm tra null đầu tiên để tránh NPE
        if (poRequests == null || poRequests.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Kiểm tra trùng mã (poCode) trong request
        Set<String> seenCodes = new HashSet<>();
        for (var req : poRequests) {
            if (!seenCodes.add(req.getPoCode())) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Trùng mã PO trong danh sách: " + req.getPoCode());
            }
        }

        // 1. Lấy danh sách hiện tại từ DB (thông qua cha)
        List<PO> currentPos = program.getPos();
        List<Long> requestIds = poRequests.stream()
                .map(EducationProgramRequestUpdateDetail.PoRequest::getId)
                .filter(Objects::nonNull) // bỏ null (PO mới)
                .toList();

        System.out.println("=== BEFORE REMOVE ===");
        currentPos.forEach(po ->
                System.out.println("PO: id=" + po.getId() + ", code=" + po.getPoCode())
        );

        currentPos.removeIf(po -> !requestIds.contains(po.getId()));

        System.out.println("=== AFTER REMOVE ===");
        currentPos.forEach(po ->
                System.out.println("PO: id=" + po.getId() + ", code=" + po.getPoCode())
        );

        for (var req : poRequests) {
            if (req.getId() != null) {

                PO existing = currentPos.stream()
                        .filter(po -> po.getId().equals(req.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Truyền id pos lạ vào: " + req.getId()));

                existing.setPoCode(req.getPoCode());
                existing.setContent(req.getContent());

            } else {
                // check PO tồn tại trong DB cùng chương trình
                boolean existsInDb = poRepository.existsByPoCodeAndEducationProgramId(req.getPoCode(), program.getId());

                if (existsInDb) {
                    // lấy PO từ DB và chỉ update nội dung
                    PO poInDb = poRepository.findByPoCodeAndEducationProgramId(req.getPoCode(), program.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "PO trùng code nhưng không tìm thấy DB"));

                    poInDb.setContent(req.getContent());
                    // không thêm mới
                    System.out.println("existsInDb: " + existsInDb);

                    poRepository.findByPoCodeAndEducationProgramId(req.getPoCode(), program.getId())
                            .ifPresentOrElse(
                                    po -> System.out.println("PO from DB: id=" + po.getId() + ", code=" + po.getPoCode() + ", hash=" + System.identityHashCode(po)),
                                    () -> System.out.println("PO from DB not found")
                            );
                } else {
                    // tạo PO mới
                    PO newPo = PO.builder()
                            .poCode(req.getPoCode())
                            .content(req.getContent())
                            .educationProgram(program)
                            .build();
                    currentPos.add(newPo);
                }
            }
        }
        System.out.println("=== FINAL currentPos BEFORE RETURN ===");

        for (PO po : currentPos) {
            System.out.println(
                    "PO: id=" + po.getId() +
                            ", code=" + po.getPoCode() +
                            ", hash=" + System.identityHashCode(po)
            );
        }
        return currentPos;
    }

    private List<PLO> updatePlos(EducationProgram program, List<EducationProgramRequestUpdateDetail.PloRequest> ploRequests) {
        // 1. Kiểm tra null/empty đầu tiên
        if (ploRequests == null || ploRequests.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Kiểm tra trùng mã (ploCode) trong request để đảm bảo tính duy nhất
        Set<String> seenCodes = new HashSet<>();
        for (var req : ploRequests) {
            if (!seenCodes.add(req.getPloCode())) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Trùng mã PLO trong danh sách: " + req.getPloCode());
            }
        }

        // 3. Lấy danh sách PLO hiện tại từ chương trình đào tạo
        List<PLO> currentPlos = program.getPlos();

        // 4. Lọc các ID từ request để xác định những PLO nào cần giữ lại
        List<Long> requestIds = ploRequests.stream()
                .map(EducationProgramRequestUpdateDetail.PloRequest::getId)
                .filter(Objects::nonNull) // Lọc bỏ các PLO mới (chưa có ID)
                .toList();

        // 5. Xóa những PLO hiện tại không còn nằm trong danh sách request
        currentPlos.removeIf(plo -> plo.getId() != null && !requestIds.contains(plo.getId()));

        // 6. Duyệt qua từng request để thực hiện Cập nhật hoặc Thêm mới
        // Xử lý PLO
        for (var req : ploRequests) {
            if (req.getId() != null) {
                // Cập nhật PLO hiện có
                PLO existing = currentPlos.stream()
                        .filter(plo -> plo.getId().equals(req.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy PLO với ID: " + req.getId()));

                existing.setPloCode(req.getPloCode());
                existing.setContent(req.getContent());

            } else {
                // check PLO đã tồn tại trong DB cùng chương trình
                boolean existsInDb = ploRepository.existsByPloCodeAndEducationProgramId(req.getPloCode(), program.getId());

                if (existsInDb) {
                    // Lấy PLO từ DB và chỉ update content
                    PLO ploInDb = ploRepository.findByPloCodeAndEducationProgramId(req.getPloCode(), program.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "PLO trùng code nhưng không tìm thấy DB"));

                    ploInDb.setContent(req.getContent());
                } else {
                    // Tạo PLO mới
                    PLO newPlo = PLO.builder()
                            .ploCode(req.getPloCode())
                            .content(req.getContent())
                            .educationProgram(program)
                            .build();
                    currentPlos.add(newPlo);
                }
            }
        }

        return currentPlos;
    }

    @Transactional
    private List<PloPoMapping> updateMapping(EducationProgram program,
                                             List<EducationProgramRequestUpdateDetail.PloPoMappingRequest> requests,
                                             List<PO> pos, List<PLO> plos) {

        // 1. Xóa mapping cũ trong DB
        ploPoMappingRepository.deleteByEducationProgramId(program.getId());
//        ploPoMappingRepository.flush(); // chắc chắn DB đã sạch

        if (requests == null || requests.isEmpty()) return null;

        Map<String, PO> poMap = pos.stream()
                .collect(Collectors.toMap(PO::getPoCode, p -> p));
        Map<String, PLO> ploMap = plos.stream()
                .collect(Collectors.toMap(PLO::getPloCode, p -> p));

        List<PloPoMapping> newMappings = new ArrayList<>();

        for (var req : requests) {
            PO po = poMap.get(req.getPoCode());
            PLO plo = ploMap.get(req.getPloCode());

            if (po == null || plo == null) continue;

            // ❗ tạo mapping mới với ID tự gen
            PloPoMapping mapping = PloPoMapping.builder()
                    .po(po)
                    .plo(plo)
                    .weight(req.getWeight())
                    .build();

            newMappings.add(mapping);

            // ❗ Thêm vào collection nếu bạn cần quan hệ 2 chiều
            po.getMappings().add(mapping);
            plo.getMappings().add(mapping);
        }

        // 2. Lưu batch tất cả mapping mới
        return ploPoMappingRepository.saveAll(newMappings);
    }
}