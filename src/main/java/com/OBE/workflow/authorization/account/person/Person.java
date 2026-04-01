package com.OBE.workflow.authorization.account.person;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ca_nhan")
public abstract class Person {

    @Id
    @Column(name = "ma_ca_nhan")
    private String id;

    @NonNull
    @Column(name = "ho_ten", nullable = false)
    private String fullName;

    @NonNull
    @Column(name = "gioi_tinh", nullable = false)
    private String gender;
}