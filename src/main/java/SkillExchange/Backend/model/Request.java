package SkillExchange.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teacherId;

    private Long learnerId;

    private Long skillId;

    // OPEN → MATCHED → CLOSED
    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "OPEN";
    }

    // getters
    public Long getId() { return id; }
    public Long getLearnerId() { return learnerId; }
    public Long getSkillId() { return skillId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getTeacherId() { return teacherId; }
    // setters
    public void setId(Long id) { this.id = id; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}