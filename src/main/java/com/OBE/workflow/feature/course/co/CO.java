package com.OBE.workflow.feature.course.co;

import com.OBE.workflow.feature.course.course_version.CourseVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "co")
public class CO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_co", nullable = false)
    private String code; // Ví dụ: CO1, CO2

    @Column(name = "noi_dung_muc_tieu", columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung mục tiêu: Phân tích và mô hình hóa...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan"),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban")
    })
    @JsonIgnore // Chặn đệ quy khi trả về API
    private CourseVersion courseVersion;
}
