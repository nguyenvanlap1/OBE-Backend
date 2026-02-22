package com.OBE.workflow.feature.officer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, String>, JpaSpecificationExecutor<Officer> {
}
