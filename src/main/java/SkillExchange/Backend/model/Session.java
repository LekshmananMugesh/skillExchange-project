package SkillExchange.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long learnerId;

    private Long teacherId;

    private Long skillId;

    private String meetLink;

    private LocalDateTime scheduledAt;

    // PENDING → ACTIVE → COMPLETED / INVALID
    private String status;

    private Boolean learnerConfirmed = false;

    private Boolean teacherConfirmed = false;

    private LocalDateTime learnerJoinedAt;

    private LocalDateTime teacherJoinedAt;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // getters
    public Long getId() {
        return id;
    }
    public Long getLearnerId() {
        return learnerId;
    }
    public Long getTeacherId() {
        return teacherId;
    }
    public Long getSkillId() {
        return skillId;
    }
    public String getMeetLink() {
        return meetLink;
    }
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    public String getStatus() {
        return status;
    }
    public Boolean getLearnerConfirmed() {
        return learnerConfirmed;
    }
    public Boolean getTeacherConfirmed() {
        return teacherConfirmed;
    }
    public LocalDateTime getLearnerJoinedAt() {
        return learnerJoinedAt;
    }
    public LocalDateTime getTeacherJoinedAt() {
        return teacherJoinedAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }




    // setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
    public void setMeetLink(String meetLink) {
        this.meetLink = meetLink;
    }
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setLearnerConfirmed(Boolean learnerConfirmed) {
        this.learnerConfirmed = learnerConfirmed;
    }
    public void setTeacherConfirmed(Boolean teacherConfirmed) {
        this.teacherConfirmed = teacherConfirmed;
    }
    public void setLearnerJoinedAt(LocalDateTime learnerJoinedAt) {
        this.learnerJoinedAt = learnerJoinedAt;
    }
    public void setTeacherJoinedAt(LocalDateTime teacherJoinedAt) {
        this.teacherJoinedAt = teacherJoinedAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}