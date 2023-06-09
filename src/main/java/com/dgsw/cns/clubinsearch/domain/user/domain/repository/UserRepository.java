package com.dgsw.cns.clubinsearch.domain.user.domain.repository;

import com.dgsw.cns.clubinsearch.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountId(String accountId);
}
