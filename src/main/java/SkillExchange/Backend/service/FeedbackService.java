package SkillExchange.Backend.service;

import SkillExchange.Backend.model.Feedback;
import SkillExchange.Backend.model.Session;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.FeedbackRepository;
import SkillExchange.Backend.repository.SessionRepository;
import SkillExchange.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    // ── SUBMIT FEEDBACK ──────────────────────────
    public String submitFeedback(
            String email,
            Long sessionId,
            int rating,
            String comment) {

        // get user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // get session
        Session session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Session not found")
                );

        // check session is completed
        if (!session.getStatus().equals("COMPLETED")) {
            return "Session is not completed yet";
        }

        // check if user is part of this session
        if (!user.getId().equals(session.getLearnerId()) &&
                !user.getId().equals(session.getTeacherId())) {
            return "You are not part of this session";
        }

        // check if already submitted feedback
        if (feedbackRepository
                .existsBySessionIdAndGivenBy(
                        sessionId, user.getId())) {
            return "You already submitted feedback";
        }

        // check rating is between 1 and 5
        if (rating < 1 || rating > 5) {
            return "Rating must be between 1 and 5";
        }

        // save feedback
        Feedback feedback = new Feedback();
        feedback.setSessionId(sessionId);
        feedback.setGivenBy(user.getId());
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedbackRepository.save(feedback);

        return "Feedback submitted successfully";
    }

    // ── GET FEEDBACK FOR SESSION ─────────────────
    public List<Map<String, String>> getSessionFeedback(
            Long sessionId) {

        List<Feedback> feedbackList = feedbackRepository
                .findBySessionId(sessionId);

        return feedbackList.stream().map(f -> {
            Map<String, String> map = new HashMap<>();
            map.put("rating",
                    String.valueOf(f.getRating()));
            map.put("comment", f.getComment());
            map.put("givenBy",
                    f.getGivenBy().toString());
            map.put("createdAt",
                    f.getCreatedAt().toString());
            return map;
        }).toList();
    }

    // ── GET TEACHER AVERAGE RATING ───────────────
    public Map<String, String> getTeacherRating(
            Long teacherId) {

        // get all sessions where this user was teacher
        List<Session> sessions = sessionRepository
                .findByTeacherId(teacherId);

        if (sessions.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            map.put("averageRating", "No sessions yet");
            return map;
        }

        // get all session ids
        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        // get all feedback for these sessions
        List<Feedback> feedbackList = feedbackRepository
                .findBySessionIdIn(sessionIds);

        if (feedbackList.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            map.put("averageRating", "No feedback yet");
            return map;
        }

        // calculate average rating
        double average = feedbackList.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        // get teacher name
        User teacher = userRepository
                .findById(teacherId)
                .orElseThrow(() ->
                        new RuntimeException("Teacher not found")
                );

        Map<String, String> map = new HashMap<>();
        map.put("teacherName", teacher.getName());
        map.put("totalSessions",
                String.valueOf(sessions.size()));
        map.put("totalFeedbacks",
                String.valueOf(feedbackList.size()));
        map.put("averageRating",
                String.format("%.1f", average));

        return map;
    }
}