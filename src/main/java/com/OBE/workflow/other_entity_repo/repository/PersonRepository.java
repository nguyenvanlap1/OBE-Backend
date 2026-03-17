package com.OBE.workflow.other_entity_repo.repository;

import com.OBE.workflow.other_entity_repo.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, String> {
}
