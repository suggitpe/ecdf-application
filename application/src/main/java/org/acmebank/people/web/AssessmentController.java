package org.acmebank.people.web;

import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
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
import java.util.stream.Collectors;

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
        User developer = userRepository.findById(evidence.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Developer not found"));
        model.addAttribute("evidence", evidence);
        model.addAttribute("developer", developer);
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
        Map<Pillar, org.acmebank.people.domain.EvidenceRating> scores = buildAssessmentScores(selectedPillars, request);
        
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
        User user = resolveUser(principal);
        
        // 1. Evidence from direct reports that needs review
        List<User> reports = userRepository.findByManagerId(user.id());
        List<Evidence> teamEvidence = new ArrayList<>();
        for (User report : reports) {
            teamEvidence.addAll(evidenceRepository.findByUserIdAndStatus(report.id(), EvidenceStatus.SUBMITTED));
        }
        
        // 2. Assessments explicitly assigned to this user (as Manager or ITA)
        List<Assessment> pendingAssessments = assessmentRepository.findByAssessorId(user.id())
                .stream()
                .filter(a -> a.assessedScores() == null || a.assessedScores().isEmpty())
                .collect(Collectors.toList());

        Map<UUID, String> userNames = new HashMap<>();
        for (Evidence evidence : teamEvidence) {
            userRepository.findById(evidence.userId()).ifPresent(u -> userNames.put(evidence.id(), u.fullName()));
        }
        Map<UUID, String> evidenceTitles = new HashMap<>();
        for (Evidence evidence : teamEvidence) {
            evidenceTitles.put(evidence.id(), evidence.title());
        }
        Map<UUID, String> evidenceStatuses = new HashMap<>();
        for (Assessment assessment : pendingAssessments) {
            evidenceRepository.findById(assessment.evidenceId()).ifPresent(evidence -> {
                evidenceTitles.put(assessment.id(), evidence.title());
                evidenceStatuses.put(assessment.id(), evidence.status().name());
                userRepository.findById(evidence.userId()).ifPresent(userReport -> userNames.put(assessment.id(), userReport.fullName()));
            });
        }

        model.addAttribute("teamEvidence", teamEvidence);
        model.addAttribute("pendingAssessments", pendingAssessments);
        model.addAttribute("userNames", userNames);
        model.addAttribute("evidenceTitles", evidenceTitles);
        model.addAttribute("evidenceStatuses", evidenceStatuses);
        
        return "assessor-queue";
    }

    private User resolveUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Map<Pillar, org.acmebank.people.domain.EvidenceRating> buildAssessmentScores(List<String> selectedPillars, HttpServletRequest request) {
        if (selectedPillars == null || selectedPillars.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Pillar, org.acmebank.people.domain.EvidenceRating> result = new HashMap<>();
        for (String pillarName : selectedPillars) {
            try {
                Pillar pillar = Pillar.valueOf(pillarName.toUpperCase());
                String scoreKey = "scores[" + pillarName + "]";
                String rationaleKey = "rationales[" + pillarName + "]";
                String scoreStr = request.getParameter(scoreKey);
                String rationale = request.getParameter(rationaleKey);
                
                if (scoreStr != null && !scoreStr.isBlank() && rationale != null && !rationale.isBlank()) {
                    int scoreVal = Integer.parseInt(scoreStr);
                    result.put(pillar, new org.acmebank.people.domain.EvidenceRating(new Score(scoreVal), rationale));
                }
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
