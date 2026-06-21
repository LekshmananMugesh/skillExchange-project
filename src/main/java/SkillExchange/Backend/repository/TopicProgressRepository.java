package SkillExchange.Backend.repository;

import SkillExchange.Backend.model.TopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicProgressRepository
        extends JpaRepository<TopicProgress, Long> {

    // get all topics for a user — used in Topics Learnt page
    // returns everything across all skill categories
    List<TopicProgress> findByUserId(Long userId);

    // get topics for a specific skill under a user
    // used to show progress within one skill e.g. all Python topics
    List<TopicProgress> findByUserIdAndSkillName(
            Long userId, String skillName);

    // check if this exact topic already exists for the user
    // prevents duplicate entries like adding "Variables" twice
    boolean existsByUserIdAndSkillNameAndTopicName(
            Long userId, String skillName, String topicName);

    // count topics under each skill — used for progress bar
    // e.g. user has covered 7 topics in Python
    long countByUserIdAndSkillName(
            Long userId, String skillName);
}