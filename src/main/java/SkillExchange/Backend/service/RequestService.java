package SkillExchange.Backend.service;

import SkillExchange.Backend.model.Request;
import SkillExchange.Backend.model.Skill;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.model.UserSkill;
import SkillExchange.Backend.repository.RequestRepository;
import SkillExchange.Backend.repository.SkillRepository;
import SkillExchange.Backend.repository.UserRepository;
import SkillExchange.Backend.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    // ── CREATE REQUEST ───────────────────────────
    public Map<String, String> createRequest(
            String email, String skillName) {

        Map<String, String> response = new HashMap<>();

        // get learner
        User learner = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // check if skill exists
        Skill skill = skillRepository
                .findByName(skillName)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Skill not found")
                );

        // save request with status OPEN
        Request request = new Request();
        request.setLearnerId(learner.getId());
        request.setSkillId(skill.getId());
        requestRepository.save(request);

        // find teacher who teaches this skill
        // and is not the learner themselves
        Optional<UserSkill> matchedTeacher =
                userSkillRepository
                        .findAll()
                        .stream()
                        .filter(us ->
                                us.getSkillId().equals(skill.getId()) &&
                                        us.getType().equals("teach") &&
                                        !us.getUserId().equals(learner.getId())
                        )
                        .findFirst();

        if (matchedTeacher.isEmpty()) {
            response.put("status", "OPEN");
            response.put("message",
                    "Request posted. No teacher found yet.");
            return response;
        }

        // get teacher user
        User teacher = userRepository
                .findById(matchedTeacher.get().getUserId())
                .orElseThrow(() ->
                        new RuntimeException("Teacher not found")
                );

        // save teacherId and status MATCHED
        request.setStatus("MATCHED");
        request.setTeacherId(teacher.getId());
        requestRepository.save(request);

        // verify it saved
        System.out.println("Request saved with teacherId: "
                + teacher.getId());

        response.put("status", "MATCHED");
        response.put("message", "Teacher found!");
        response.put("teacherName", teacher.getName());
        response.put("teacherEmail", teacher.getEmail());
        response.put("requestId",
                request.getId().toString());
        response.put("teacherId",
                teacher.getId().toString());

        return response;
    }

    // ── GET MY REQUESTS ──────────────────────────
    public List<Map<String, String>> getMyRequests(
            String email) {

        User learner = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<Request> requests = requestRepository
                .findByLearnerId(learner.getId());

        return requests.stream().map(req -> {
            Map<String, String> map = new HashMap<>();
            map.put("requestId", req.getId().toString());
            map.put("status", req.getStatus());
            map.put("createdAt",
                    req.getCreatedAt().toString());
            skillRepository.findById(req.getSkillId())
                    .ifPresent(s ->
                            map.put("skillName", s.getName())
                    );
            return map;
        }).toList();
    }
}