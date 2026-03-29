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
        if (poRequests == null) return new ArrayList<>();

        // 1. Lấy danh sách hiện tại và xác định ID từ request
        List<PO> currentPos = program.getPos();

        Set<Long> assignedIds = new HashSet<>();
        for (var req : poRequests) {
            if (req.getId() != null) assignedIds.add(req.getId());
        }

        for (var req : poRequests) {
            if (req.getId() == null) {
                currentPos.stream()
                        .filter(po -> po.getPoCode().equalsIgnoreCase(req.getPoCode()))
                        .filter(po -> !assignedIds.contains(po.getId()))
                        .findFirst()
                        .ifPresent(existing -> {
                            req.setId(existing.getId());
                            assignedIds.add(existing.getId());
                        });
            }
        }

        Set<Long> requestIds = poRequests.stream()
                .map(EducationProgramRequestUpdateDetail.PoRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 2. BƯỚC XÓA: Xác định các PO cũ không còn trong request
        List<PO> toDelete = currentPos.stream()
                .filter(po -> !requestIds.contains(po.getId()))
                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            List<Long> deleteIds = toDelete.stream().map(PO::getId).toList();

            // Gỡ khỏi list cha (RAM)
            currentPos.removeAll(toDelete);

            // Xóa triệt để trong DB (InBatch để dứt khoát)
            poRepository.deleteAllInBatch(toDelete);
            poRepository.flush(); // Quan trọng để giải phóng poCode cũ ngay lập tức
        }

        // 3. BƯỚC MÃ TẠM (Swap Logic): Chống lỗi Unique Constraint khi đổi tên PO1 <-> PO2
        long tempSuffix = System.currentTimeMillis();
        for (var req : poRequests) {
            if (req.getId() != null) {
                currentPos.stream()
                        .filter(po -> po.getId().equals(req.getId()))
                        .findFirst()
                        .ifPresent(po -> po.setPoCode(po.getPoCode() + "_tmp_" + tempSuffix));
            }
        }
        poRepository.flush(); // Đẩy toàn bộ mã tạm xuống DB

        // 4. BƯỚC CẬP NHẬT CHÍNH THỨC & THÊM MỚI
        for (var req : poRequests) {
            if (req.getId() != null) {
                // CẬP NHẬT: Mã gốc giờ đã trống chỗ, an tâm set lại
                PO existing = currentPos.stream()
                        .filter(po -> po.getId().equals(req.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy PO ID: " + req.getId()));

                existing.setPoCode(req.getPoCode());
                existing.setContent(req.getContent());
            } else {
                // THÊM MỚI: Check trùng code trong memory trước (đề phòng request gửi 2 mã mới giống nhau)
                boolean isDuplicateInMem = currentPos.stream()
                        .anyMatch(p -> p.getPoCode().equalsIgnoreCase(req.getPoCode()));

                if (isDuplicateInMem) {
                    // Nếu trùng mã với một cái vừa được thêm/update, ta cập nhật nội dung cho nó
                    currentPos.stream()
                            .filter(p -> p.getPoCode().equalsIgnoreCase(req.getPoCode()))
                            .findFirst()
                            .ifPresent(p -> p.setContent(req.getContent()));
                } else {
                    // Tạo mới hoàn toàn
                    PO newPo = PO.builder()
                            .poCode(req.getPoCode())
                            .content(req.getContent())
                            .educationProgram(program)
                            .build();
                    currentPos.add(newPo);
                }
            }
        }

        return currentPos;
    }

    private List<PLO> updatePlos(EducationProgram program, List<EducationProgramRequestUpdateDetail.PloRequest> ploRequests) {
        if (ploRequests == null) return new ArrayList<>();

        // 1. Lấy danh sách hiện tại và xác định ID từ request
        List<PLO> currentPlos = program.getPlos();

        // --- BƯỚC 0: TIỀN XỬ LÝ CỨU ID THÔNG MINH ---
        Set<Long> assignedIds = new HashSet<>();
        for (var req : ploRequests) {
            if (req.getId() != null) assignedIds.add(req.getId());
        }

        for (var req : ploRequests) {
            if (req.getId() == null) {
                currentPlos.stream()
                        .filter(plo -> plo.getPloCode().equalsIgnoreCase(req.getPloCode()))
                        .filter(plo -> !assignedIds.contains(plo.getId()))
                        .findFirst()
                        .ifPresent(existing -> {
                            req.setId(existing.getId());
                            assignedIds.add(existing.getId());
                        });
            }
        }

        Set<Long> requestIds = ploRequests.stream()
                .map(EducationProgramRequestUpdateDetail.PloRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 2. BƯỚC XÓA: Xác định các PLO cũ cần loại bỏ
        List<PLO> toDelete = currentPlos.stream()
                .filter(plo -> !requestIds.contains(plo.getId()))
                .collect(Collectors.toList());

        if (!toDelete.isEmpty()) {
            List<Long> deleteIds = toDelete.stream().map(PLO::getId).toList();

            // --- QUAN TRỌNG: Xóa Mapping liên quan đến PLO trước khi xóa PLO ---
            // Ví dụ: ploPoMappingRepository.deleteByPloIds(deleteIds);
            // ploPoMappingRepository.flush();

            // Gỡ khỏi list cha (RAM) và xóa trong DB
            currentPlos.removeAll(toDelete);
            ploRepository.deleteAllInBatch(toDelete);
            ploRepository.flush(); // Giải phóng ploCode cũ ngay lập tức
        }

        // 3. BƯỚC MÃ TẠM (Swap Logic): Chống lỗi Unique khi đổi PLO1 <-> PLO2
        long tempSuffix = System.currentTimeMillis();
        for (var req : ploRequests) {
            if (req.getId() != null) {
                currentPlos.stream()
                        .filter(plo -> plo.getId().equals(req.getId()))
                        .findFirst()
                        .ifPresent(plo -> plo.setPloCode(plo.getPloCode() + "_tmp_" + tempSuffix));
            }
        }
        ploRepository.flush();

        // 4. BƯỚC CẬP NHẬT CHÍNH THỨC & THÊM MỚI
        for (var req : ploRequests) {
            if (req.getId() != null) {
                // UPDATE: Mã gốc đã trống chỗ, an tâm set lại mã thật
                PLO existing = currentPlos.stream()
                        .filter(plo -> plo.getId().equals(req.getId()))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy PLO ID: " + req.getId()));

                existing.setPloCode(req.getPloCode());
                existing.setContent(req.getContent());
            } else {
                // INSERT: Kiểm tra trùng mã trong memory (đề phòng request gửi 2 mã mới giống nhau)
                boolean isDuplicateInMem = currentPlos.stream()
                        .anyMatch(p -> p.getPloCode().equalsIgnoreCase(req.getPloCode()));

                if (isDuplicateInMem) {
                    currentPlos.stream()
                            .filter(p -> p.getPloCode().equalsIgnoreCase(req.getPloCode()))
                            .findFirst()
                            .ifPresent(p -> p.setContent(req.getContent()));
                } else {
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