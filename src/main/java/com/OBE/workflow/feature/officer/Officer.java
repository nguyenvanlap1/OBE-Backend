package com.OBE.workflow.feature.officer;

import com.OBE.workflow.authorization.account.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "can_bo")
@PrimaryKeyJoinColumn(name = "ma_ca_nhan")
public class Officer extends Person {
}