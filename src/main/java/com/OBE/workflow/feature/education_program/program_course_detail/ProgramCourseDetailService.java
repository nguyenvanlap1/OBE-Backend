package com.OBE.workflow.feature.education_program.program_course_detail;

import com.OBE.workflow.conmon.exception.AppException;
import com.OBE.workflow.conmon.exception.ErrorCode;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.course_version.CourseVersionId;
import com.OBE.workflow.feature.course_version.CourseVersionRepository;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.EducationProgramRepository;
import com.OBE.workflow.feature.education_program.knowledge_block.KnowledgeBlock;
import com.OBE.workflow.feature.education_program.knowledge_block.KnowledgeBlockRepository;
import com.OBE.workflow.feature.education_program.response.ProgramCourseDetailListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramCourseDetailService {

    private final ProgramCourseDetailRepository detailRepository;
    private final EducationProgramRepository programRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final KnowledgeBlockRepository knowledgeBlockRepository;

    @Transactional
    public ProgramCourseDetailResponse updateKnowledgeBlock(String programId, String courseId, String newKnowledgeBlockId) {
        // 1. Tìm bản ghi mapping hiện có
        ProgramCourseDetail detail = detailRepository.findByEducationProgramIdAndCourseVersionCourseId(programId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Học phần này chưa có trong chương trình đào tạo"));

        // 2. Tìm khối kiến thức mới
        KnowledgeBlock newKb = knowledgeBlockRepository.findById(newKnowledgeBlockId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy Khối kiến thức mới"));

        // 3. Cập nhật và lưu
        detail.setKnowledgeBlock(newKb);
        return ProgramCourseDetailResponse.fromEntity(detailRepository.save(detail));
    }

    @Transactional
    public ProgramCourseDetailResponse addCourseToProgram(String programId, String courseId, Integer versionNumber, String knowledgeBlockId) {
        // 1. Check trùng bằng Repo (Nhanh hơn dùng stream trên List của Program)
        if (detailRepository.existsByEducationProgramIdAndCourseVersionCourseId(programId, courseId)) {
            throw new AppException(ErrorCode.INVALID_KEY, "Học phần đã tồn tại trong CTĐT");
        }

        // 2. Lấy các Entity liên quan (DùnggetReferenceById để tối ưu nếu chỉ cần ID)
        EducationProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy CTĐT"));

        Integer finalVersion = (versionNumber != null) ? versionNumber :
                courseVersionRepository.findFirstByCourseIdOrderByVersionNumberDesc(courseId)
                        .map(CourseVersion::getVersionNumber)
                        .orElse(0);

        CourseVersion courseVersion = courseVersionRepository.findById(new CourseVersionId(courseId, finalVersion))
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy phiên bản"));

        KnowledgeBlock kb = knowledgeBlockId != null ?
                knowledgeBlockRepository.findById(knowledgeBlockId).orElse(null) : null;

        // 3. Lưu trực tiếp qua DetailRepo
        ProgramCourseDetail detail = ProgramCourseDetail.builder()
                .educationProgram(program)
                .courseVersion(courseVersion)
                .knowledgeBlock(kb)
                .build();

        return ProgramCourseDetailResponse.fromEntity(detailRepository.save(detail));
    }

    @Transactional
    public void removeCourseFromProgram(String programId, String courseId) {
        // Xóa trực tiếp bằng Query trong Repo (Cực kỳ nhanh)
        // Bạn cần viết thêm hàm deleteByEducationProgramIdAndCourseVersionCourseId trong Repo
        detailRepository.deleteByEducationProgramIdAndCourseVersionCourseId(programId, courseId);
    }

    @Transactional(readOnly = true)
    public ProgramCourseDetailListResponse getCourseDetailsByProgramId(String programId) {
        EducationProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND, "Không tìm thấy chương trình đào tạo"));

        // Sử dụng hàm static từEntity đã viết trong DTO
        return ProgramCourseDetailListResponse.fromEntity(program);
    }
}