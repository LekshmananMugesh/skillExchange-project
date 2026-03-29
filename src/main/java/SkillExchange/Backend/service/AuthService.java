package SkillExchange.Backend.service;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // ── REGISTER ────────────────────────────────
    public String register(String name,
                           String email,
                           String password) {

        // check if email already used
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }

        // create new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        // hash password before saving
        user.setPassword(
                passwordEncoder.encode(password)
        );

        // credits = 3 is already default in User.java
        // save to MySQL
        userRepository.save(user);

        return "Registered successfully";
    }

    // ── LOGIN ────────────────────────────────────
    public String login(String email, String password) {

        // find user by email
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // compare entered password with hashed password
        if (!passwordEncoder.matches(
                password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        // generate token
        String token = jwtUtil.generateToken(email);

        // save token in Redis for 24 hours
        redisTemplate.opsForValue().set(
                "token:" + email,
                token,
                24,
                TimeUnit.HOURS
        );

        return token;
    }

    // ── LOGOUT ───────────────────────────────────
    public String logout(String email) {

        // delete token from Redis
        redisTemplate.delete("token:" + email);

        return "Logged out successfully";
    }
}