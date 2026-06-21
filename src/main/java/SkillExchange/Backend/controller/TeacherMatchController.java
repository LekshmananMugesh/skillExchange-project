package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.dto.TeacherMatchDTO;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.UserRepository;
import SkillExchange.Backend.service.TeacherMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherMatchController {

    @Autowired
    private TeacherMatchService teacherMatchService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }

    // GET /api/teachers/match?skillName=Python
    // returns sorted list of teachers for a given skill
    // sorted by credits ascending — fairness mechanism
    // excludes the requesting user from the list
    @GetMapping("/match")
    public List<TeacherMatchDTO> getTeachers(
            @RequestHeader("Authorization") String header,
            @RequestParam String skillName) {

        String email = getEmail(header);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return teacherMatchService
                .getTeachersForSkill(
                        skillName, user.getId()
                );
    }

    // GET /api/teachers/skills
    // returns all skill names that have at least one teacher
    // used to populate the skill search dropdown in frontend
    @GetMapping("/skills")
    public List<String> getTeachableSkills() {
        return teacherMatchService.getAllTeachableSkills();
    }
}