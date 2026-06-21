package SkillExchange.Backend.dto;

import java.util.List;

public class TeacherMatchDTO {

    private Long teacherId;
    private String teacherName;
    private int credits;
    private String photoUrl;
    private double averageRating;
    private int totalSessions;
    private int totalFeedbacks;
    private List<String> teachingSkills;

    // getters
    public Long getTeacherId() { return teacherId; }
    public String getTeacherName() { return teacherName; }
    public int getCredits() { return credits; }
    public String getPhotoUrl() { return photoUrl; }
    public double getAverageRating() { return averageRating; }
    public int getTotalSessions() { return totalSessions; }
    public int getTotalFeedbacks() { return totalFeedbacks; }
    public List<String> getTeachingSkills() { return teachingSkills; }

    // setters
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    public void setTotalFeedbacks(int totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }
    public void setTeachingSkills(List<String> teachingSkills) { this.teachingSkills = teachingSkills; }
}