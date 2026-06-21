package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.dto.TopicProgressDTO;
import SkillExchange.Backend.service.TopicTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicTrackerController {

    @Autowired
    private TopicTrackerService topicTrackerService;

    @Autowired
    private JwtUtil jwtUtil;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // POST /api/topics/add
    // log a topic the user has studied
    // body: { skillName, topicName, dateStudied (optional) }
    @PostMapping("/add")
    public TopicProgressDTO addTopic(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> body) {

        String email = getEmail(header);
        String skillName = body.get("skillName");
        String topicName = body.get("topicName");

        // parse optional date — defaults to today in service
        LocalDate dateStudied = null;
        if (body.get("dateStudied") != null
                && !body.get("dateStudied").isEmpty()) {
            dateStudied = LocalDate.parse(
                    body.get("dateStudied")
            );
        }

        return topicTrackerService.addTopic(
                email, skillName, topicName, dateStudied
        );
    }

    // GET /api/topics/all
    // returns all topics grouped by skill name
    // used to render the Topics Learnt page
    @GetMapping("/all")
    public Map<String, List<TopicProgressDTO>> getAllTopics(
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        return topicTrackerService.getGroupedTopics(email);
    }

    // GET /api/topics/skill/{skillName}
    // returns all topics for a single skill
    @GetMapping("/skill/{skillName}")
    public List<TopicProgressDTO> getTopicsBySkill(
            @RequestHeader("Authorization") String header,
            @PathVariable String skillName) {

        String email = getEmail(header);
        return topicTrackerService
                .getTopicsBySkill(email, skillName);
    }

    // GET /api/topics/progress
    // returns topic count per skill
    // used to render progress bars on the tracker page
    @GetMapping("/progress")
    public Map<String, Long> getProgress(
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        return topicTrackerService.getProgressSummary(email);
    }

    // DELETE /api/topics/{topicId}
    // remove a topic entry — only the owner can delete it
    @DeleteMapping("/{topicId}")
    public String deleteTopic(
            @RequestHeader("Authorization") String header,
            @PathVariable Long topicId) {

        String email = getEmail(header);
        return topicTrackerService
                .deleteTopic(email, topicId);
    }
}