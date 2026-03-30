package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository
        extends JpaRepository<Request, Long> {

    // get all requests by learner
    List<Request> findByLearnerId(Long learnerId);

    // get open requests for a skill
    List<Request> findBySkillIdAndStatus(
            Long skillId, String status);
}