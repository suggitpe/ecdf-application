package org.acmebank.people.web;

import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.EvidenceRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.acmebank.people.domain.service.AssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentRepository assessmentRepository;
    private final EvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentRepository assessmentRepository,
                                EvidenceRepository evidenceRepository,
                                UserRepository userRepository,
                                AssessmentService assessmentService) {
        this.assessmentRepository = assessmentRepository;
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
        this.assessmentService = assessmentService;
    }

    @GetMapping("/{evidenceId}")
    public String showAssessmentForm(@PathVariable UUID evidenceId, Model model) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evidence not found"));
        model.addAttribute("evidence", evidence);
        model.addAttribute("pillars", Pillar.values());
        return "assessment-form";
    }

    @PostMapping("/{evidenceId}")
    public String submitAssessment(@PathVariable UUID evidenceId,
                                  @RequestParam String reviewSummary,
                                  @RequestParam(value = "pillars", required = false) List<String> selectedPillars,
                                  HttpServletRequest request,
                                  Principal principal) {
        User assessor = resolveUser(principal);
        Map<Pillar, Score> scores = buildAssessmentScores(selectedPillars, request);
        
        assessmentService.submitAssessment(evidenceId, assessor.id(), scores, reviewSummary);
        
        return "redirect:/assessment/queue";
    }

    @PostMapping("/{evidenceId}/assign")
    public String assignAssessor(@PathVariable UUID evidenceId,
                                 @RequestParam UUID assessorId) {
        assessmentService.assignThirdPartyAssessor(evidenceId, assessorId);
        return "redirect:/assessment/queue";
    }

    @GetMapping("/queue")
    public String showQueue(Principal principal, Model model) {
        User assessor = resolveUser(principal);
        List<Assessment> assessments = assessmentRepository.findByAssessorId(assessor.id());
        model.addAttribute("assessments", assessments);
        return "assessor-queue";
    }

    private User resolveUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Map<Pillar, Score> buildAssessmentScores(List<String> selectedPillars, HttpServletRequest request) {
        if (selectedPillars == null || selectedPillars.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Pillar, Score> result = new HashMap<>();
        for (String pillarName : selectedPillars) {
            try {
                Pillar pillar = Pillar.valueOf(pillarName.toUpperCase());
                String scoreKey = "scores[" + pillarName + "]";
                String scoreStr = request.getParameter(scoreKey);
                if (scoreStr != null && !scoreStr.isBlank()) {
                    int scoreVal = Integer.parseInt(scoreStr);
                    result.put(pillar, new Score(scoreVal));
                }
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
