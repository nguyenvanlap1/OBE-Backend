package com.OBE.workflow.feature.education_program.mapping;

import com.OBE.workflow.feature.education_program.plo.PLO;
import com.OBE.workflow.feature.course_version.co.CO;
import com.OBE.workflow.feature.education_program.program_course_detail.ProgramCourseDetail;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "plo_co_mapping",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_plo_co_detail",
                columnNames = {"plo_id", "co_id", "ma_so_chi_tiet"}
        )
)
public class PloCoMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Khóa ngoại tới PLO (Chuẩn đầu ra chương trình)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plo_id", nullable = false)
    private PLO plo;

    // 2. Khóa ngoại tới CO (Mục tiêu học phần)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_id", nullable = false)
    private CO co;

    // 3. Khóa ngoại tới Chi tiết CTĐT (Xác nhận môn này thuộc chương trình này)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_so_chi_tiet", nullable = false)
    private ProgramCourseDetail programCourseDetail;

    @Column(name = "weight")
    private Double weight; // Trọng số nếu bạn muốn tính toán định lượng
}