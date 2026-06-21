package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.UserRepository;
import SkillExchange.Backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // POST /api/notifications/credit-update
    // manually trigger a credit balance push to a user's navbar
    // used for testing — real updates fire automatically after session
    @PostMapping("/credit-update")
    public String sendCreditUpdate(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, Object> body) {

        String email = getEmail(header);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        notificationService.sendCreditUpdate(
                user.getId(),
                user.getCredits()
        );

        return "Credit update sent to user "
                + user.getId();
    }

    // POST /api/notifications/user
    // send a generic notification to any user by ID
    @PostMapping("/user")
    public String sendUserNotification(
            @RequestBody Map<String, String> body) {

        Long userId = Long.parseLong(body.get("userId"));
        String type = body.get("type");
        String message = body.get("message");

        notificationService.sendUserNotification(
                userId, type, message
        );

        return "Notification sent to user " + userId;
    }
}