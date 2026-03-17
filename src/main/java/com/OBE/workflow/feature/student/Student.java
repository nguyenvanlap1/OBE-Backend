package com.OBE.workflow.feature.student;

import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.other_entity_repo.entity.Person;
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
@Table(name = "sinh_vien")
@PrimaryKeyJoinColumn(name = "ma_ca_nhan")
public class Student extends Person {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sinh_vien_chuong_trinh", // Bảng trung gian
            joinColumns = @JoinColumn(name = "ma_sinh_vien"),
            inverseJoinColumns = @JoinColumn(name = "ma_chuong_trinh")
    )
    private Set<EducationProgram> educationPrograms = new HashSet<>();
}