package SkillExchange.Backend.service;

import SkillExchange.Backend.dto.TopicProgressDTO;
import SkillExchange.Backend.model.TopicProgress;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.TopicProgressRepository;
import SkillExchange.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicTrackerService {

    @Autowired
    private TopicProgressRepository topicProgressRepository;

    @Autowired
    private UserRepository userRepository;

    // ── ADD TOPIC ────────────────────────────────────────
    // user logs a topic they have studied
    // prevents duplicates — same skill + topic cannot be added twice
    // date defaults to today if not provided
    public TopicProgressDTO addTopic(String email,
                                     String skillName,
                                     String topicName,
                                     LocalDate dateStudied) {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        // duplicate check — same skill + topic already exists
        if (topicProgressRepository
                .existsByUserIdAndSkillNameAndTopicName(
                    user.getId(), skillName, topicName)) {
            throw new RuntimeException(
                "Topic already added for this skill"
            );
        }

        TopicProgress topic = new TopicProgress();
        topic.setUserId(user.getId());
        topic.setSkillName(skillName);
        topic.setTopicName(topicName);
        // if frontend sends null, @PrePersist fills today
        topic.setDateStudied(
            dateStudied != null ? dateStudied : LocalDate.now()
        );

        topicProgressRepository.save(topic);

        return toDTO(topic);
    }

    // ── GET ALL TOPICS FOR USER ──────────────────────────
    // returns every topic entry grouped by skill name
    // used to render the Topics Learnt page
    // Map key = skill name, value = list of topics under it
    public Map<String, List<TopicProgressDTO>> getGroupedTopics(
            String email) {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        List<TopicProgress> all =
            topicProgressRepository.findByUserId(user.getId());

        // LinkedHashMap preserves insertion order
        // so skills appear in the order they were first added
        Map<String, List<TopicProgressDTO>> grouped =
            new LinkedHashMap<>();

        for (TopicProgress tp : all) {
            grouped
                .computeIfAbsent(
                    tp.getSkillName(),
                    k -> new ArrayList<>()
                )
                .add(toDTO(tp));
        }

        return grouped;
    }

    // ── GET TOPICS BY SKILL ──────────────────────────────
    // returns all topics under one specific skill for a user
    // used when user clicks into a skill on the tracker page
    public List<TopicProgressDTO> getTopicsBySkill(
            String email, String skillName) {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        return topicProgressRepository
            .findByUserIdAndSkillName(user.getId(), skillName)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    // ── GET PROGRESS SUMMARY ─────────────────────────────
    // returns topic count per skill
    // used to render the progress bar on the tracker page
    // e.g. { "Python": 7, "Java": 3, "Guitar": 12 }
    public Map<String, Long> getProgressSummary(String email) {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        List<TopicProgress> all =
            topicProgressRepository.findByUserId(user.getId());

        // count topics per skill using stream grouping
        Map<String, Long> summary = new LinkedHashMap<>();
        for (TopicProgress tp : all) {
            summary.merge(tp.getSkillName(), 1L, Long::sum);
        }

        return summary;
    }

    // ── DELETE TOPIC ─────────────────────────────────────
    // removes a topic entry by its ID
    // verifies it belongs to the requesting user first
    public String deleteTopic(String email, Long topicId) {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        TopicProgress topic = topicProgressRepository
            .findById(topicId)
            .orElseThrow(() ->
                new RuntimeException("Topic not found"));

        // ownership check — cannot delete someone else's topic
        if (!topic.getUserId().equals(user.getId())) {
            throw new RuntimeException(
                "You can only delete your own topics"
            );
        }

        topicProgressRepository.delete(topic);
        return "Topic deleted successfully";
    }

    // ── HELPER — entity to DTO ───────────────────────────
    private TopicProgressDTO toDTO(TopicProgress tp) {
        TopicProgressDTO dto = new TopicProgressDTO();
        dto.setId(tp.getId());
        dto.setUserId(tp.getUserId());
        dto.setSkillName(tp.getSkillName());
        dto.setTopicName(tp.getTopicName());
        dto.setDateStudied(tp.getDateStudied());
        return dto;
    }
}