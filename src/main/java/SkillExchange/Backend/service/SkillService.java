package SkillExchange.Backend.service;

import SkillExchange.Backend.model.Skill;
import SkillExchange.Backend.model.UserSkill;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.SkillRepository;
import SkillExchange.Backend.repository.UserSkillRepository;
import SkillExchange.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserRepository userRepository;

    // ── ADD SKILL ────────────────────────────────
    public String addSkill(String email,
                           String skillName,
                           String type) {

        // get user by email
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // check if skill exists in skills table
        // if not → create it
        Skill skill = skillRepository
                .findByName(skillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    return skillRepository.save(newSkill);
                });

        // check if user already added this skill
        if (userSkillRepository
                .existsByUserIdAndSkillIdAndType(
                        user.getId(), skill.getId(), type)) {
            return "Skill already added";
        }

        // save in user_skills table
        UserSkill userSkill = new UserSkill();
        userSkill.setUserId(user.getId());
        userSkill.setSkillId(skill.getId());
        userSkill.setType(type);
        userSkillRepository.save(userSkill);

        return "Skill added successfully";
    }

    // ── GET MY SKILLS ────────────────────────────
    public List<String> getMySkills(String email,
                                    String type) {

        // get user by email
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // get all user skills by type
        List<UserSkill> userSkills = userSkillRepository
                .findByUserIdAndType(user.getId(), type);

        // convert skill ids to skill names
        return userSkills.stream()
                .map(us -> skillRepository
                        .findById(us.getSkillId())
                        .map(Skill::getName)
                        .orElse("Unknown"))
                .toList();
    }
}