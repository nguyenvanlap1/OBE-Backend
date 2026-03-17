package com.OBE.workflow.feature.course.clo;

import com.OBE.workflow.feature.course.co.CO;
import com.OBE.workflow.feature.course.course_version.CourseVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clo")
public class CLO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_clo", nullable = false)
    private String code;

    @Column(name = "noi_dung_chuan_dau_ra", columnDefinition = "TEXT", nullable = false)
    private String content;

    // 1. Liên kết tới CourseVersion (Cha)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan"),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban")
    })
    @JsonIgnore // Tránh đệ quy khi lấy dữ liệu từ CourseVersion
    private CourseVersion courseVersion;
}