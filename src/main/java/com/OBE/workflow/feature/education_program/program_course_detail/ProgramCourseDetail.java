package com.OBE.workflow.feature.education_program.program_course_detail;
import com.OBE.workflow.feature.course_version.CourseVersion;
import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.education_program.knowledge_block.KnowledgeBlock;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "chi_tiet_chuong_trinh_dao_tao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_program_course",
                        columnNames = {"ma_chuong_trinh_dao_tao", "ma_hoc_phan"}
                )
        }
)
public class ProgramCourseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_so_chi_tiet")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chuong_trinh_dao_tao", nullable = false)
    private EducationProgram educationProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan", nullable = false),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban", nullable = false)
    })
    private CourseVersion courseVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_so_khoi_kien_thuc")
    private KnowledgeBlock knowledgeBlock;
}
