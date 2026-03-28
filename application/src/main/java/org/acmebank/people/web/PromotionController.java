package org.acmebank.people.web;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.domain.*;
import org.acmebank.people.domain.port.*;
import org.acmebank.people.domain.service.PromotionService;
import org.acmebank.people.domain.service.PromotionPeriodService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final PromotionPeriodService promotionPeriodService;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    @GetMapping("/propose")
    public String showProposeForm(@RequestParam UUID candidateId, Principal principal, Model model) {
        User manager = resolveUser(principal);
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));

        if (candidate.managerId() == null || !candidate.managerId().equals(manager.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to propose this candidate.");
        }

        PromotionPeriod activePeriod = promotionPeriodService.getActivePeriod()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No active promotion period found."));

        List<Grade> grades = gradeRepository.findAll();

        model.addAttribute("candidate", candidate);
        model.addAttribute("grades", grades);
        model.addAttribute("activePeriod", activePeriod);
        
        return "promotion-propose";
    }

    @PostMapping("/propose")
    public String proposeCandidate(@RequestParam UUID candidateId,
                                   @RequestParam UUID targetGradeId,
                                   @RequestParam String rationale,
                                   Principal principal) {
        User manager = resolveUser(principal);
        promotionService.proposeCandidate(candidateId, manager.id(), targetGradeId, rationale);
        return "redirect:/";
    }

    private User resolveUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
