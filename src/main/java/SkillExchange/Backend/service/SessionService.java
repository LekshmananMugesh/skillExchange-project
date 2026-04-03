package SkillExchange.Backend.service;

import SkillExchange.Backend.model.Request;
import SkillExchange.Backend.model.Session;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.RequestRepository;
import SkillExchange.Backend.repository.SessionRepository;
import SkillExchange.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // ── CREATE SESSION ───────────────────────────
    public Map<String, String> createSession(
            Long requestId,
            String meetLink,
            String scheduledAt) {

        // find the request
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException("Request not found")
                );

        // check request is matched
        if (!request.getStatus().equals("MATCHED")) {
            throw new RuntimeException(
                    "Request is not matched yet"
            );
        }

        // get learner and teacher
        User learner = userRepository
                .findById(request.getLearnerId())
                .orElseThrow(() ->
                        new RuntimeException("Learner not found")
                );

        // find teacher from request service
        // teacherId is stored in request
        // for now we pass it directly
        // we will fix this by storing teacherId in request

        // create session
        Session session = new Session();
        session.setTeacherId(request.getTeacherId());
        session.setMeetLink(meetLink);
        session.setScheduledAt(
                LocalDateTime.parse(scheduledAt)
        );
        sessionRepository.save(session);

        // update request to CLOSED
        request.setStatus("CLOSED");
        requestRepository.save(request);

        // send email to learner
        sendEmail(
                learner.getEmail(),
                "SkillExchange - Session Scheduled",
                "Hi " + learner.getName() + ",\n\n" +
                        "Your session has been scheduled.\n" +
                        "Time: " + scheduledAt + "\n" +
                        "Meet Link: " + meetLink + "\n\n" +
                        "Please join within 10 minutes of the scheduled time.\n" +
                        "Session must last at least 30 minutes.\n\n" +
                        "SkillExchange Team"
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "Session created successfully");
        response.put("sessionId", session.getId().toString());
        response.put("meetLink", meetLink);
        response.put("scheduledAt", scheduledAt);
        return response;
    }

    // ── JOIN SESSION ─────────────────────────────
    public String joinSession(Long sessionId, String email) {

        // find session
        Session session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Session not found")
                );

        // get user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // check if session is still pending
        if (!session.getStatus().equals("PENDING")) {
            return "Session is already " + session.getStatus();
        }

        // check join time — must be within 10 minutes
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledAt = session.getScheduledAt();
        LocalDateTime deadline = scheduledAt.plusMinutes(10);

        if (now.isAfter(deadline)) {
            session.setStatus("INVALID");
            sessionRepository.save(session);
            return "Session INVALID — joined after 10 minutes";
        }

        // mark who joined
        if (user.getId().equals(session.getLearnerId())) {
            session.setLearnerJoinedAt(now);
        } else {
            session.setTeacherJoinedAt(now);
        }

        // if both joined → set ACTIVE
        if (session.getLearnerJoinedAt() != null &&
                session.getTeacherJoinedAt() != null) {
            session.setStatus("ACTIVE");
        }

        sessionRepository.save(session);
        return "Joined successfully";
    }

    // ── CONFIRM SESSION ──────────────────────────
    public String confirmSession(Long sessionId, String email) {

        // find session
        Session session = sessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Session not found")
                );

        // get user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // check session is active
        if (!session.getStatus().equals("ACTIVE")) {
            return "Session is not active yet";
        }

        // check duration — must be 30 minutes
        LocalDateTime joinTime =
                user.getId().equals(session.getLearnerId())
                        ? session.getLearnerJoinedAt()
                        : session.getTeacherJoinedAt();

        long minutes = java.time.Duration
                .between(joinTime, LocalDateTime.now())
                .toMinutes();

        if (minutes < 30) {
            return "Session must last at least 30 minutes. " +
                    "Current: " + minutes + " minutes";
        }

        // mark confirmed
        if (user.getId().equals(session.getLearnerId())) {
            session.setLearnerConfirmed(true);
        } else {
            session.setTeacherConfirmed(true);
        }

        // if both confirmed → COMPLETED → transfer credits
        if (session.getLearnerConfirmed() &&
                session.getTeacherConfirmed()) {

            session.setStatus("COMPLETED");
            sessionRepository.save(session);

            // transfer credits
            transferCredits(
                    session.getLearnerId(),
                    session.getTeacherId()
            );

            return "Session COMPLETED. Credits transferred!";
        }

        sessionRepository.save(session);
        return "Confirmed. Waiting for other user to confirm.";
    }

    // ── GET MY SESSIONS ──────────────────────────
    public List<Session> getMySessions(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // get sessions as learner
        List<Session> sessions = sessionRepository
                .findByLearnerId(user.getId());

        // add sessions as teacher
        sessions.addAll(
                sessionRepository.findByTeacherId(user.getId())
        );

        return sessions;
    }

    // ── TRANSFER CREDITS ─────────────────────────
    private void transferCredits(
            Long learnerId, Long teacherId) {

        User learner = userRepository
                .findById(learnerId)
                .orElseThrow(() ->
                        new RuntimeException("Learner not found")
                );

        User teacher = userRepository
                .findById(teacherId)
                .orElseThrow(() ->
                        new RuntimeException("Teacher not found")
                );

        // learner loses 1 credit
        learner.setCredits(learner.getCredits() - 1);

        // teacher gains 1 credit
        teacher.setCredits(teacher.getCredits() + 1);

        userRepository.save(learner);
        userRepository.save(teacher);
    }

    // ── SEND EMAIL ───────────────────────────────
    private void sendEmail(
            String to, String subject, String body) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // email failed — just log it
            // don't crash the app
            System.out.println(
                    "Email failed: " + e.getMessage()
            );
        }
    }
}