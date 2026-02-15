package com.OBE.workflow.repository;

import com.OBE.workflow.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    // Trong file AccountRepository.java
    Optional<Account> findFirstByIsSystemAccountOrderByUsernameAsc(boolean isSystemAccount);
}
