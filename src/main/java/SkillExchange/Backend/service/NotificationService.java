package SkillExchange.Backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ── CREDIT UPDATE ────────────────────────────────────
    // called after session completes
    // pushes new credit balance to both users navbar live
    // frontend subscribes to /topic/credits/{userId}
    public void sendCreditUpdate(Long userId, int newBalance) {
        messagingTemplate.convertAndSend(
            "/topic/credits/" + userId,
            Map.of(
                "type",    "CREDIT_UPDATE",
                "userId",  userId,
                "credits", newBalance
            )
        );
    }

    // ── SESSION STATUS UPDATE ────────────────────────────
    // called when session becomes ACTIVE or COMPLETED
    // both users are notified on the same channel
    // frontend auto-navigates learner to feedback on COMPLETED
    public void sendSessionUpdate(Long sessionId,
                                  String status,
                                  Long learnerId,
                                  Long teacherId) {
        messagingTemplate.convertAndSend(
            "/topic/session/" + sessionId,
            Map.of(
                "type",      "SESSION_UPDATE",
                "sessionId", sessionId,
                "status",    status,
                "learnerId", learnerId,
                "teacherId", teacherId
            )
        );
    }

    // ── TEACHER NOTIFICATION ─────────────────────────────
    // fires when a learner posts a request and teacher is matched
    // teacher sees instant popup without refreshing their page
    // frontend subscribes to /topic/teacher/{teacherId}
    public void sendTeacherNotification(Long teacherId,
                                        String learnerName,
                                        String skillName,
                                        Long requestId) {
        messagingTemplate.convertAndSend(
            "/topic/teacher/" + teacherId,
            Map.of(
                "type",        "NEW_REQUEST",
                "teacherId",   teacherId,
                "learnerName", learnerName,
                "skillName",   skillName,
                "requestId",   requestId,
                "message",     learnerName
                               + " wants to learn "
                               + skillName
            )
        );
    }

    // ── GENERIC NOTIFICATION ─────────────────────────────
    // utility method for any custom message to a user
    // used for things like session reminders or rejections
    public void sendUserNotification(Long userId,
                                     String type,
                                     String message) {
        messagingTemplate.convertAndSend(
            "/topic/user/" + userId,
            Map.of(
                "type",    type,
                "userId",  userId,
                "message", message
            )
        );
    }
}