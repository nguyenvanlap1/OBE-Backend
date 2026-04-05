package com.OBE.workflow.feature.student;

import com.OBE.workflow.feature.course_section.enrollment.Enrollment;
import com.OBE.workflow.feature.student_class.StudentClass;
import com.OBE.workflow.conmon.authorization.account.person.Person;
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

    // Thêm quan hệ tới bảng Đăng ký học phần (Enrollment)
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private Set<Enrollment> enrollments = new HashSet<>();
}