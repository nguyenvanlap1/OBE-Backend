package com.OBE.workflow.other_entity_repo.entity.repository;

import com.OBE.workflow.other_entity_repo.entity.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, String> {
}
