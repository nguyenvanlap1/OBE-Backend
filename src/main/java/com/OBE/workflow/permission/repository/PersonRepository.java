package com.OBE.workflow.permission.repository;

import com.OBE.workflow.permission.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, String> {
}
