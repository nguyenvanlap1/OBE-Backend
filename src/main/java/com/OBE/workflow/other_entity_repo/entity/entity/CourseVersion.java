package com.OBE.workflow.other_entity_repo.entity.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoc_phan_phien_ban")
public class CourseVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ma_hoc_phan", nullable = false)
    @NonNull
    private Course course;

    @Column(name = "ten_hoc_phan", nullable = false)
    @NonNull
    private String name;

    @Column(name = "so_tin_chi", nullable = false)
    @NonNull
    private Integer credits;

    @Column(name = "ap_dung_tu_nam", nullable = false)
    @NonNull
    private Integer fromYear;

    @Column(name = "ap_dung_den_nam")
    private Integer toYear; // Cho phép null vì có thể đang còn hiệu lực
}