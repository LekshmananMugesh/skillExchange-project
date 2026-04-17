package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.model.Session;
import SkillExchange.Backend.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private JwtUtil jwtUtil;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // POST /api/sessions/create
    @PostMapping("/create")
    public Map<String, String> createSession(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> body) {

        return sessionService.createSession(
                Long.parseLong(body.get("requestId")),
                body.get("meetLink"),
                body.get("scheduledAt")
        );
    }

    // POST /api/sessions/join/{sessionId}
    @PostMapping("/join/{sessionId}")
    public String joinSession(
            @PathVariable Long sessionId,
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        return sessionService.joinSession(sessionId, email);
    }

    // POST /api/sessions/confirm/{sessionId}
    @PostMapping("/confirm/{sessionId}")
    public String confirmSession(
            @PathVariable Long sessionId,
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        return sessionService.confirmSession(sessionId, email);
    }

    // GET /api/sessions/my-sessions
    @GetMapping("/my-sessions")
    public List<Session> getMySessions(
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        return sessionService.getMySessions(email);
    }
}