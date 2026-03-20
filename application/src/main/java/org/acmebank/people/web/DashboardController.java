package org.acmebank.people.web;

import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.EvidenceRepository;
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
import java.util.Map;
import java.util.Optional;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final CheckInRepository checkInRepository;
    private final EvidenceRepository evidenceRepository;

    public DashboardController(UserRepository userRepository, CheckInRepository checkInRepository, EvidenceRepository evidenceRepository) {
        this.userRepository = userRepository;
        this.checkInRepository = checkInRepository;
        this.evidenceRepository = evidenceRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<CheckIn> checkIns = checkInRepository.findByUserId(user.id());
        List<CheckIn> sortedCheckIns = checkIns.stream()
                .sorted(Comparator.comparing(CheckIn::checkInDate).reversed())
                .toList();
        CheckIn latestCheckIn = sortedCheckIns.isEmpty() ? null : sortedCheckIns.get(0);

        List<Evidence> recentEvidence = evidenceRepository.findByUserId(user.id()).stream()
                .sorted(Comparator.comparing(Evidence::createdDate).reversed())
                .limit(5)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("latestCheckIn", latestCheckIn);
        model.addAttribute("historicalCheckIns", sortedCheckIns);
        model.addAttribute("recentEvidence", recentEvidence);
        model.addAttribute("pillars", Pillar.values());

        List<String> radarLabels = new ArrayList<>();
        List<Integer> radarData = new ArrayList<>();

        for (Pillar pillar : Pillar.values()) {
            radarLabels.add(pillar.name());
            int scoreValue = 0;
            if (latestCheckIn != null && latestCheckIn.holisticScores() != null) {
                Score score = latestCheckIn.holisticScores().get(pillar);
                if (score != null) {
                    scoreValue = score.value();
                }
            }
            radarData.add(scoreValue);
        }

        model.addAttribute("radarLabels", radarLabels);
        model.addAttribute("radarData", radarData);

        return "dashboard";
    }
}
