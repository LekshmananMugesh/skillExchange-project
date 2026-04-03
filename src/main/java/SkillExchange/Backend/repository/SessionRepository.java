package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository
        extends JpaRepository<Session, Long> {

    // get all sessions for a learner
    List<Session> findByLearnerId(Long learnerId);

    // get all sessions for a teacher
    List<Session> findByTeacherId(Long teacherId);
}