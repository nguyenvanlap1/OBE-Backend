package com.OBE.workflow.feature.auth;

import com.OBE.workflow.other_entity_repo.entity.entity.Account;
import com.OBE.workflow.other_entity_repo.entity.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    // Trong file AccountRepository.java
    Optional<Account> findFirstByIsSystemAccountOrderByUsernameAsc(boolean isSystemAccount);

    boolean existsByPerson(Person person);
}
