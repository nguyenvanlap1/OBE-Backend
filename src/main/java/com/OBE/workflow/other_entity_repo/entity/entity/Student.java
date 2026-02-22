package com.OBE.workflow.other_entity_repo.entity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "sinh_vien")
@PrimaryKeyJoinColumn(name = "ma_ca_nhan") // Dùng chung khóa với Person
public class Student extends Person {
}