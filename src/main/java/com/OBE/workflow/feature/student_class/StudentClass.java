package com.OBE.workflow.feature.student_class;

import com.OBE.workflow.feature.education_program.EducationProgram;
import com.OBE.workflow.feature.school_year.SchoolYear;
import com.OBE.workflow.feature.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lop_sinh_vien")
public class StudentClass {

    @Id
    @Column(name = "ma_lop")
    private String id; // VD: "DI2296A1", "KTPM01_K48"

    @Column(name = "ten_lop", nullable = false)
    private String name; // VD: "Kỹ thuật phần mềm 1 - Khóa 48"

    // --- Relationships ---

    // Một lớp phải thuộc về một Niên khóa cụ thể (VD: K48)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa", nullable = false)
    private SchoolYear schoolYear;

    // Một lớp phải học theo một Chương trình đào tạo cụ thể
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chuong_trinh_dao_tao", nullable = false)
    private EducationProgram educationProgram;

    // Lớp quản lý danh sách sinh viên (Owner of the relationship)
    @ManyToMany
    @JoinTable(
            name = "lop_sinh_vien_chi_tiet",
            joinColumns = @JoinColumn(name = "ma_lop"),
            inverseJoinColumns = @JoinColumn(name = "ma_sinh_vien")
    )
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    // --- Helper Methods ---

    /**
     * Thêm sinh viên vào lớp và cập nhật ngược lại phía Sinh viên
     */
    public void addStudent(Student student) {
        this.students.add(student);
        student.getStudentClasses().add(this);
    }

    /**
     * Xóa sinh viên khỏi lớp và cập nhật ngược lại phía Sinh viên
     */
    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getStudentClasses().remove(this);
    }
}
