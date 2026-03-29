package com.OBE.workflow.feature.student;

import com.OBE.workflow.feature.student_class.StudentClass;
import com.OBE.workflow.permission.entity.Person;
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
    // Sinh viên chỉ đối chiếu xem mình thuộc những lớp nào
    @ManyToMany(mappedBy = "students")
    @Builder.Default
    private Set<StudentClass> studentClasses = new HashSet<>();
}