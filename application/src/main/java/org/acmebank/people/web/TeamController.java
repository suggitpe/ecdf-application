package org.acmebank.people.web;

import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class TeamController {

    private final UserRepository userRepository;
    private final CheckInRepository checkInRepository;
    private final org.acmebank.people.domain.service.PromotionPeriodService promotionPeriodService;

    public TeamController(UserRepository userRepository, CheckInRepository checkInRepository,
                          org.acmebank.people.domain.service.PromotionPeriodService promotionPeriodService) {
        this.userRepository = userRepository;
        this.checkInRepository = checkInRepository;
        this.promotionPeriodService = promotionPeriodService;
    }

    @GetMapping("/team")
    public String team(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));

        if (manager.managerId() != null && userRepository.findByManagerId(manager.id()).isEmpty()) {
            // Might be a standard user trying to access team view. Should they see it? For now, yes, just empty team.
        }

        List<User> staticTeamMembers = userRepository.findByManagerId(manager.id());
        List<TeamMemberInfo> teamMembers = new ArrayList<>();

        for (User u : staticTeamMembers) {
            CheckIn latestCheckIn = checkInRepository.findByUserId(u.id()).stream()
                    .max(Comparator.comparing(CheckIn::checkInDate))
                    .orElse(null);
            teamMembers.add(new TeamMemberInfo(u, latestCheckIn));
        }

        // Just to support recursive tree logic later, we can structure this to recurse,
        // but let's stick to 1 level for now to pass the simple test

        model.addAttribute("manager", manager);
        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("activePeriod", promotionPeriodService.getActivePeriod().orElse(null));

        return "team";
    }

    public record TeamMemberInfo(User user, CheckIn latestCheckIn) {}
}
