package com.officina.officina_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);
    
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByIdAndDeletedFalse(Long userId);
}
