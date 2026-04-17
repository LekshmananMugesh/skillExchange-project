package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtUtil jwtUtil;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // POST /api/feedback/submit
    @PostMapping("/submit")
    public String submitFeedback(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> body) {

        String email = getEmail(header);

        return feedbackService.submitFeedback(
                email,
                Long.parseLong(body.get("sessionId")),
                Integer.parseInt(body.get("rating")),
                body.get("comment")
        );
    }

    // GET /api/feedback/session/{sessionId}
    @GetMapping("/session/{sessionId}")
    public List<Map<String, String>> getSessionFeedback(
            @PathVariable Long sessionId) {

        return feedbackService
                .getSessionFeedback(sessionId);
    }

    // GET /api/feedback/teacher/{teacherId}
    @GetMapping("/teacher/{teacherId}")
    public Map<String, String> getTeacherRating(
            @PathVariable Long teacherId) {

        return feedbackService
                .getTeacherRating(teacherId);
    }
}