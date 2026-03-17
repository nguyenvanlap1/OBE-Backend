package com.OBE.workflow.feature.course.assessment_component;

import com.OBE.workflow.feature.course.clo.CLO;
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
@Table(name = "diem_thanh_phan")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_thanh_phan", nullable = false)
    private String name; // Ví dụ: Thi lý thuyết cuối kỳ, Bài tập...

    @Column(name = "quy_dinh")
    private String regulation; // Ví dụ: Bắt buộc

    @Column(name = "trong_so", nullable = false)
    private Double weight; // Ví dụ: 0.5 (tương đương 50%)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ma_hoc_phan", referencedColumnName = "ma_hoc_phan"),
            @JoinColumn(name = "so_thu_tu_phien_ban", referencedColumnName = "so_thu_tu_phien_ban")
    })
    @JsonIgnore
    private CourseVersion courseVersion;
    // Sau này bạn sẽ cần thêm @ManyToMany với CLOs để đánh giá chuẩn đầu ra như trong ảnh
}
