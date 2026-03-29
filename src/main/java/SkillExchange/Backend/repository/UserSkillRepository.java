package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkillRepository
        extends JpaRepository<UserSkill, Long> {

    // get all skills for a user by type
    List<UserSkill> findByUserIdAndType(
            Long userId, String type);

    // check if user already added this skill
    boolean existsByUserIdAndSkillIdAndType(
            Long userId, Long skillId, String type);
}