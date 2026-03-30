package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private JwtUtil jwtUtil;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // POST /api/requests/create
    @PostMapping("/create")
    public Map<String, String> createRequest(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> body) {

        String email = getEmail(header);
        return requestService.createRequest(
                email,
                body.get("skillName")
        );
    }

    // GET /api/requests/my-requests
    @GetMapping("/my-requests")
    public List<Map<String, String>> getMyRequests(
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        return requestService.getMyRequests(email);
    }
}