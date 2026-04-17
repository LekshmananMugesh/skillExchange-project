package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {

    // get all feedback for a session
    List<Feedback> findBySessionId(Long sessionId);

    // check if user already gave feedback
    boolean existsBySessionIdAndGivenBy(
            Long sessionId, Long givenBy);

    // get all feedback given to a teacher
    // by finding sessions where teacher was involved
    List<Feedback> findBySessionIdIn(
            List<Long> sessionIds);
}