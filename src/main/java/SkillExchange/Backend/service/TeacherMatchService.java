package SkillExchange.Backend.service;

import SkillExchange.Backend.dto.TeacherMatchDTO;
import SkillExchange.Backend.model.Skill;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.model.UserSkill;
import SkillExchange.Backend.repository.FeedbackRepository;
import SkillExchange.Backend.repository.SessionRepository;
import SkillExchange.Backend.repository.SkillRepository;
import SkillExchange.Backend.repository.UserRepository;
import SkillExchange.Backend.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherMatchService {

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    // ── GET SORTED TEACHER LIST ──────────────────────────
    // returns all teachers who teach the given skill
    // sorted by credits ascending — teachers with fewer
    // credits appear first so new teachers get equal opportunity
    // this is the fairness mechanism described in the project
    public List<TeacherMatchDTO> getTeachersForSkill(
            String skillName, Long currentUserId) {

        // find the skill record first
        Optional<Skill> skillOpt =
            skillRepository.findByName(skillName);

        if (skillOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Skill skill = skillOpt.get();

        // get all UserSkill records where type is teach
        // for this specific skill using the new repo method
        List<UserSkill> teacherSkills =
            userSkillRepository.findBySkillIdAndType(
                skill.getId(), "teach"
            );

        List<TeacherMatchDTO> result = new ArrayList<>();

        for (UserSkill us : teacherSkills) {

            // exclude the requesting user from the list
            // a user cannot teach themselves
            if (us.getUserId().equals(currentUserId)) {
                continue;
            }

            Optional<User> teacherOpt =
                userRepository.findById(us.getUserId());

            if (teacherOpt.isEmpty()) continue;

            User teacher = teacherOpt.get();

            // calculate average rating for this teacher
            // by looking at all their completed sessions
            List<Long> sessionIds = sessionRepository
                .findByTeacherId(teacher.getId())
                .stream()
                .map(s -> s.getId())
                .toList();

            double avgRating = 0.0;
            int totalFeedbacks = 0;

            if (!sessionIds.isEmpty()) {
                var feedbacks = feedbackRepository
                    .findBySessionIdIn(sessionIds);
                totalFeedbacks = feedbacks.size();
                if (totalFeedbacks > 0) {
                    avgRating = feedbacks.stream()
                        .mapToInt(f -> f.getRating())
                        .average()
                        .orElse(0.0);
                }
            }

            // get all skills this teacher offers
            // shown on the teacher card in the UI
            List<String> teachingSkills =
                userSkillRepository
                    .findByUserIdAndType(teacher.getId(), "teach")
                    .stream()
                    .map(tus -> skillRepository
                        .findById(tus.getSkillId())
                        .map(Skill::getName)
                        .orElse("Unknown"))
                    .toList();

            // build the DTO
            TeacherMatchDTO dto = new TeacherMatchDTO();
            dto.setTeacherId(teacher.getId());
            dto.setTeacherName(teacher.getName());
            dto.setCredits(teacher.getCredits());
            dto.setPhotoUrl(teacher.getPhotoUrl());
            dto.setAverageRating(
                Math.round(avgRating * 10.0) / 10.0
            );
            dto.setTotalSessions(sessionIds.size());
            dto.setTotalFeedbacks(totalFeedbacks);
            dto.setTeachingSkills(teachingSkills);

            result.add(dto);
        }

        // sort by credits ascending — fairness mechanism
        // teachers with fewer credits shown first
        result.sort(Comparator.comparingInt(
            TeacherMatchDTO::getCredits
        ));

        return result;
    }

    // ── GET ALL AVAILABLE SKILLS ─────────────────────────
    // returns all skill names that have at least one teacher
    // used to populate the skill search dropdown
    public List<String> getAllTeachableSkills() {
        return skillRepository.findAll()
            .stream()
            .filter(skill ->
                !userSkillRepository
                    .findBySkillIdAndType(skill.getId(), "teach")
                    .isEmpty()
            )
            .map(Skill::getName)
            .sorted()
            .toList();
    }
}