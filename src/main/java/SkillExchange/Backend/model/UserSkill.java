package SkillExchange.Backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_skills")
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which user
    private Long userId;

    // which skill
    private Long skillId;

    // "teach" or "learn"
    private String type;

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getSkillId() { return skillId; }
    public String getType() { return type; }

    // setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }
    public void setType(String type) { this.type = type; }
}