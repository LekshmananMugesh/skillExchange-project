package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private JwtUtil jwtUtil;

    // get email from token header
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // POST /api/skills/teach
    @PostMapping("/teach")
    public String addTeachSkill(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> body) {

        String email = getEmail(header);
        return skillService.addSkill(
                email,
                body.get("skillName"),
                "teach"
        );
    }

    // POST /api/skills/learn
    @PostMapping("/learn")
    public String addLearnSkill(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> body) {

        String email = getEmail(header);
        return skillService.addSkill(
                email,
                body.get("skillName"),
                "learn"
        );
    }

    // GET /api/skills/my-skills
    @GetMapping("/my-skills")
    public Map<String, List<String>> getMySkills(
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);

        List<String> teachSkills =
                skillService.getMySkills(email, "teach");

        List<String> learnSkills =
                skillService.getMySkills(email, "learn");

        return Map.of(
                "teach", teachSkills,
                "learn", learnSkills
        );
    }
}