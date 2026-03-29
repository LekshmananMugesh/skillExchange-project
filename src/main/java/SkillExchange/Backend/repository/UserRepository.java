package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    // finds user by email — used in login
    Optional<User> findByEmail(String email);

    // checks if email already exists — used in register
    boolean existsByEmail(String email);
}