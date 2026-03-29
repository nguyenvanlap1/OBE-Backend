package com.OBE.workflow.feature.education_program.mapping;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.CourseVersionId;
import com.OBE.workflow.feature.course_version.CourseVersionRepository;
import com.OBE.workflow.feature.course_version.co.CO;
import com.OBE.workflow.feature.course_version.co.CORepository;
import com.OBE.workflow.feature.education_program.mapping.dto.EducationProgramCourseSummaryList;
import com.OBE.workflow.feature.education_program.mapping.dto.PloCoMappingResponse;
import com.OBE.workflow.feature.education_program.mapping.dto.PloCoMappingResponseList;
import com.OBE.workflow.feature.education_program.plo.PLO;
import com.OBE.workflow.feature.education_program.plo.PLORepository;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetail;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PloCoMappingService {

    private final PloCoMappingRepository ploCoMappingRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final ProgramCourseDetailRepository programCourseDetailRepository;
    private final PLORepository ploRepository;
    private final CORepository coRepository;

    /**
     * Lấy danh sách mapping dựa trên cặp programId và courseId
     */
    @Transactional(readOnly = true)
    public PloCoMappingResponseList getListMapping(String programId, String courseId) {
        // 1. Tìm bản ghi chi tiết để xác định học phần này có trong CTĐT không
        // Giả sử bạn tìm theo programId và courseId (lấy version mới nhất hoặc version đang active)
        ProgramCourseDetail detail = programCourseDetailRepository
                .findByEducationProgramIdAndCourseVersionCourseId(programId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học phần không tồn tại trong chương trình đào tạo này"));

        // 2. Lấy danh sách mapping dựa trên ma_so_chi_tiet (ID của ProgramCourseDetail)
        List<PloCoMapping> mappings = ploCoMappingRepository.findByProgramCourseDetailId(detail.getId());

        // 3. Map sang DTO
        return PloCoMappingResponseList.fromEntities(mappings);
    }

    /**
     * Thêm hoặc Cập nhật một Mapping (Upsert)
     */
    @Transactional
    public PloCoMappingResponse upsertMapping(String programId, String courseId, String ploCode, String coCode, Double weight) {
        // 1. Tìm ProgramCourseDetail để xác định ngữ cảnh
        ProgramCourseDetail detail = programCourseDetailRepository
                .findByEducationProgramIdAndCourseVersionCourseId(programId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học phần không thuộc CTĐT này"));

        // 2. Tìm PLO và CO tương ứng
        PLO plo = ploRepository.findByPloCodeAndEducationProgramId(ploCode, programId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "PLO không tồn tại trong chương trình"));

        CO co = coRepository.findByCoCodeAndCourseVersion(coCode, detail.getCourseVersion())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "CO không tồn tại trong học phần"));

        // 3. Kiểm tra xem đã tồn tại mapping chưa để Update, nếu chưa thì Create
        PloCoMapping mapping = ploCoMappingRepository
                .findByPloIdAndCoIdAndProgramCourseDetailId(plo.getId(), co.getId(), detail.getId())
                .orElse(PloCoMapping.builder()
                        .plo(plo)
                        .co(co)
                        .programCourseDetail(detail)
                        .build());

        mapping.setWeight(weight);
        // mapping.setLevel("M"); // Bạn có thể thêm level nếu cần

        return PloCoMappingResponse.fromEntity(ploCoMappingRepository.save(mapping));
    }

    /**
     * Xóa một Mapping (Gỡ bỏ ánh xạ giữa PLO và CO)
     */
    @Transactional
    public void removeMapping(String programId, String courseId, String ploCode, String coCode) {
        ProgramCourseDetail detail = programCourseDetailRepository
                .findByEducationProgramIdAndCourseVersionCourseId(programId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Ngữ cảnh không hợp lệ"));

        PloCoMapping mapping = ploCoMappingRepository
                .findMappingByCodes(programId, courseId, ploCode, coCode)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy ánh xạ để xóa"));

        ploCoMappingRepository.delete(mapping);
    }

    public EducationProgramCourseSummaryList getEducationProgramCourseSummaryList(String courseId, Integer version) {
        CourseVersionId courseVersionId = new CourseVersionId(courseId, version);
        CourseVersion courseVersion = courseVersionRepository.findById(courseVersionId)
                .orElseThrow(()-> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy học phần: "+ courseId+" ,"+version));

        return EducationProgramCourseSummaryList.fromEntity(courseVersion);
    }
}