package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    // find skill by name
    Optional<Skill> findByName(String name);
}