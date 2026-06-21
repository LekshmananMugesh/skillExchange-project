package SkillExchange.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "topic_progress")
public class TopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which user added this topic entry
    @Column(name = "user_id")
    private Long userId;

    // skill category — e.g. "Python", "Java", "Guitar"
    // stored as plain text, not FK to skills table
    // because user might track topics for skills
    // they are not formally registered under
    @Column(name = "skill_name")
    private String skillName;

    // specific topic — e.g. "Functions and Scope"
    @Column(name = "topic_name")
    private String topicName;

    // date the user studied this topic
    // stored as LocalDate — no time component needed
    @Column(name = "date_studied")
    private LocalDate dateStudied;

    @PrePersist
    public void prePersist() {
        // auto-set today's date if not provided
        if (this.dateStudied == null) {
            this.dateStudied = LocalDate.now();
        }
    }

    // getters
    public Long getId()           { return id; }
    public Long getUserId()       { return userId; }
    public String getSkillName()  { return skillName; }
    public String getTopicName()  { return topicName; }
    public LocalDate getDateStudied() { return dateStudied; }

    // setters
    public void setId(Long id)                   { this.id = id; }
    public void setUserId(Long userId)           { this.userId = userId; }
    public void setSkillName(String skillName)   { this.skillName = skillName; }
    public void setTopicName(String topicName)   { this.topicName = topicName; }
    public void setDateStudied(LocalDate dateStudied) {
        this.dateStudied = dateStudied;
    }
}