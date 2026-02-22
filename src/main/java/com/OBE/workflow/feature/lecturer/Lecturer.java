package com.OBE.workflow.feature.lecturer;

import com.OBE.workflow.feature.supDepartment.SubDepartment;
import com.OBE.workflow.other_entity_repo.entity.entity.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "giang_vien")
@PrimaryKeyJoinColumn(name = "ma_ca_nhan")
public class Lecturer extends Person {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "giang_vien_bo_mon", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "ma_giang_vien"),
            inverseJoinColumns = @JoinColumn(name = "ma_bo_mon")
    )
    private Set<SubDepartment> subDepartments = new HashSet<>();
}