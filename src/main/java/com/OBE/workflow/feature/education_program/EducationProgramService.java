package com.OBE.workflow.feature.education_program;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course.course_version.CourseVersion;
import com.OBE.workflow.feature.course.course_version.CourseVersionRepository;
import com.OBE.workflow.feature.education_program.request.EducationProgramFilterRequest;
import com.OBE.workflow.feature.education_program.request.EducationProgramRequest;
import com.OBE.workflow.feature.education_program.response.EducationProgramResponse;
import com.OBE.workflow.feature.school_year.SchoolYear;
import com.OBE.workflow.feature.school_year.SchoolYearRepository;
import com.OBE.workflow.feature.sup_department.SubDepartment;
import com.OBE.workflow.feature.sup_department.SubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationProgramService {

    private final EducationProgramRepository educationProgramRepository;
    private final EducationProgramMapper educationProgramMapper;
    private final SubDepartmentRepository subDepartmentRepository;
    private final SchoolYearRepository schoolYearRepository;
    private final CourseVersionRepository courseVersionRepository;

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

        // 5. Xử lý danh sách Phiên bản học phần (ManyToMany với Composite Key)
        if (request.getCourseVersions() != null) {
            List<CourseVersion> versions = request.getCourseVersions().stream()
                    .map(vReq -> courseVersionRepository
                            .findByCourseIdAndVersionNumber(vReq.getCourseId(), vReq.getVersionNumber())
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                                    "Không tìm thấy phiên bản học phần: " + vReq.getCourseId())))
                    .toList();
            educationProgram.setCourseVersions(versions);
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

        // 5. Cập nhật danh sách Phiên bản học phần
        if (request.getCourseVersions() != null) {
            List<CourseVersion> versions = request.getCourseVersions().stream()
                    .map(vReq -> courseVersionRepository
                            .findByCourseIdAndVersionNumber(vReq.getCourseId(), vReq.getVersionNumber())
                            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND,
                                    "Không tìm thấy phiên bản học phần: " + vReq.getCourseId())))
                    .toList();
            educationProgram.setCourseVersions(versions);
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

    // Bạn có thể thêm các hàm Create, Update, Delete ở đây sau...
}