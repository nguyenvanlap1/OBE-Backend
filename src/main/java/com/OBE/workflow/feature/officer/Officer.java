package com.OBE.workflow.feature.officer;

import com.OBE.workflow.other_entity_repo.entity.Person;
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