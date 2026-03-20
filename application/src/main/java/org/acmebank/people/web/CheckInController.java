package org.acmebank.people.web;

import org.acmebank.people.domain.*;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.GradeRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.acmebank.people.domain.service.CheckInService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/checkins")
public class CheckInController {

    private final CheckInService checkInService;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    public CheckInController(
            CheckInService checkInService,
            CheckInRepository checkInRepository,
            UserRepository userRepository,
            GradeRepository gradeRepository) {
        this.checkInService = checkInService;
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/user/{userId}")
    public String checkInHistory(@PathVariable UUID userId, Model model, Authentication authentication) {
        User developer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        List<CheckIn> checkins = checkInRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(CheckIn::checkInDate).reversed())
                .toList();

        // Determine if the currently logged-in user is the manager (not the employee themselves)
        boolean isManager = false;
        if (authentication != null) {
            User caller = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (caller != null && !caller.id().equals(userId)) {
                isManager = true;
            }
        }

        model.addAttribute("developer", developer);
        model.addAttribute("checkins", checkins);
        model.addAttribute("isManager", isManager);
        return "checkin-list";
    }

    @GetMapping("/new/{userId}")
    public String newCheckInForm(@PathVariable UUID userId, Model model) {
        User developer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Map<Pillar, Score> actualScores = checkInService.getAggregatedScores(userId);

        model.addAttribute("developer", developer);
        model.addAttribute("grades", gradeRepository.findAll());
        model.addAttribute("pillars", Pillar.values());
        model.addAttribute("actualScores", actualScores);
        return "checkin-form";
    }

    @PostMapping("/new/{userId}")
    public String submitCheckIn(
            @PathVariable UUID userId,
            @RequestParam UUID targetGradeId,
            @RequestParam String managerNotes,
            Authentication authentication) {

        User manager = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Manager not found: " + authentication.getName()));

        Grade targetGrade = gradeRepository.findById(targetGradeId)
                .orElseThrow(() -> new IllegalArgumentException("Target grade not found: " + targetGradeId));

        checkInService.createCheckIn(userId, manager.id(), managerNotes, targetGrade);

        return "redirect:/checkins/user/" + userId;
    }
}
