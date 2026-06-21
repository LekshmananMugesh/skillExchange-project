package SkillExchange.Backend.dto;

import java.time.LocalDate;

public class TopicProgressDTO {

    private Long id;
    private Long userId;
    private String skillName;
    private String topicName;
    private LocalDate dateStudied;

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getSkillName() { return skillName; }
    public String getTopicName() { return topicName; }
    public LocalDate getDateStudied() { return dateStudied; }

    // setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    public void setDateStudied(LocalDate dateStudied) { this.dateStudied = dateStudied; }
}