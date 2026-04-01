package com.OBE.workflow.authorization.account;

import com.OBE.workflow.authorization.account.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    // Trong file AccountRepository.java
    Optional<Account> findFirstByIsSystemAccountOrderByUsernameAsc(boolean isSystemAccount);

    boolean existsByPerson(Person person);
}
