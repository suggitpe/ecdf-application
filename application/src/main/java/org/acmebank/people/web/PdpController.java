package org.acmebank.people.web;

import org.acmebank.people.domain.PdpItem;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.port.PdpItemRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.acmebank.people.domain.service.PdpService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/pdp")
public class PdpController {

    private final PdpItemRepository pdpItemRepository;
    private final UserRepository userRepository;
    private final PdpService pdpService;

    public PdpController(PdpItemRepository pdpItemRepository, UserRepository userRepository, PdpService pdpService) {
        this.pdpItemRepository = pdpItemRepository;
        this.userRepository = userRepository;
        this.pdpService = pdpService;
    }

    @GetMapping
    public String viewPdpDashboard(Principal principal, Model model) {
        User user = resolveUser(principal);
        
        List<PdpItem> myPdps = pdpItemRepository.findByUserId(user.id());
        
        List<User> reports = userRepository.findByManagerId(user.id());
        Map<UUID, List<PdpItem>> teamPdps = new HashMap<>();
        Map<UUID, String> userNames = new HashMap<>();
        
        for (User report : reports) {
            List<PdpItem> reportPdps = pdpItemRepository.findByUserId(report.id());
            if (!reportPdps.isEmpty()) {
                teamPdps.put(report.id(), reportPdps);
                userNames.put(report.id(), report.fullName());
            }
        }

        model.addAttribute("myPdps", myPdps);
        model.addAttribute("teamPdps", teamPdps);
        model.addAttribute("userNames", userNames);
        model.addAttribute("user", user);

        return "pdp";
    }

    @PostMapping("/{pdpId}/complete")
    public String markPdpComplete(@PathVariable UUID pdpId, Principal principal) {
        resolveUser(principal); // Enforce authenticated
        pdpService.markAsCompleted(pdpId);
        return "redirect:/pdp";
    }

    @PostMapping("/checkin/{checkInId}/create")
    public String createManualPdp(
            @PathVariable UUID checkInId,
            @RequestParam UUID userId,
            @RequestParam Pillar pillar,
            @RequestParam String gapDescription,
            @RequestParam String actionablePlan,
            Principal principal) {
        
        resolveUser(principal); // Enforce authenticated
        
        String autoLink = "https://learning.acmebank.com/search?q=" + pillar.name();
        
        pdpService.createPdpItem(userId, checkInId, pillar, gapDescription, actionablePlan, autoLink);
        
        return "redirect:/checkins/" + checkInId;
    }

    private User resolveUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
