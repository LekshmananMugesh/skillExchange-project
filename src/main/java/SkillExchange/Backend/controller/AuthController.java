package SkillExchange.Backend.controller;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.repository.UserRepository;
import SkillExchange.Backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // POST /api/auth/register
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> body) {
        return authService.register(
                body.get("name"),
                body.get("email"),
                body.get("password")
        );
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> body) {

        String token = authService.login(
                body.get("email"),
                body.get("password")
        );

        // return token as JSON
        return Map.of("token", token);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public String logout(
            @RequestHeader("Authorization") String header) {

        // remove "Bearer " and get email from token
        String token = header.substring(7);
        String email = jwtUtil.getEmail(token);

        return authService.logout(email);
    }

    // GET /api/auth/credits
    @GetMapping("/credits")
    public Map<String, Integer> getCredits(
            @RequestHeader("Authorization") String header) {

        // get email from token
        String token = header.substring(7);
        String email = jwtUtil.getEmail(token);

        // find user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return Map.of("credits", user.getCredits());
    }
}
