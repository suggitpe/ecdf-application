package org.acmebank.people.web;

import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.EvidenceRating;
import org.acmebank.people.domain.User;
import org.acmebank.people.domain.port.EvidenceRepository;
import org.acmebank.people.domain.port.UserRepository;
import org.acmebank.people.domain.service.EvidenceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/evidence")
public class EvidenceController {

    private final UserRepository userRepository;
    private final EvidenceRepository evidenceRepository;
    private final EvidenceService evidenceService;
    private final String storagePath;

    public EvidenceController(
            UserRepository userRepository,
            EvidenceRepository evidenceRepository,
            EvidenceService evidenceService,
            @Value("${app.storage.path:./data/storage}") String storagePath) {
        this.userRepository = userRepository;
        this.evidenceRepository = evidenceRepository;
        this.evidenceService = evidenceService;
        this.storagePath = storagePath;
    }

    // -------------------------------------------------------------------------
    // GET /evidence — list evidence for the logged-in user
    // -------------------------------------------------------------------------
    @GetMapping
    public String listEvidence(Principal principal, Model model) {
        User user = resolveUser(principal);
        List<Evidence> evidenceList = evidenceRepository.findByUserId(user.id()).stream()
                .sorted(Comparator.comparing(Evidence::createdDate).reversed())
                .toList();
        model.addAttribute("evidenceList", evidenceList);
        return "evidence-list";
    }

    // -------------------------------------------------------------------------
    // GET /evidence/new — show create form
    // -------------------------------------------------------------------------
    @GetMapping("/new")
    public String newEvidenceForm(Principal principal, Model model) {
        model.addAttribute("pillars", Pillar.values());
        model.addAttribute("currentUser", resolveUser(principal));
        return "evidence-form";
    }

    // -------------------------------------------------------------------------
    // POST /evidence/new — submit create form (multipart)
    // -------------------------------------------------------------------------
    @PostMapping("/new")
    public String createEvidence(
            Principal principal,
            HttpServletRequest request,
            @RequestParam("title") String title,
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "impact", defaultValue = "") String impact,
            @RequestParam(value = "complexity", defaultValue = "") String complexity,
            @RequestParam(value = "contribution", defaultValue = "") String contribution,
            @RequestParam(value = "pillars", required = false) List<String> selectedPillars,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            Model model) {

        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            User user = resolveUser(principal);
            model.addAttribute("pillars", Pillar.values());
            model.addAttribute("currentUser", user);
            model.addAttribute("error", "Complete Title and Description are required.");
            return "evidence-form";
        }

        User user = resolveUser(principal);
        
        // Build self-assessment map from submitted scores
        Map<Pillar, EvidenceRating> selfAssessment = buildSelfAssessment(selectedPillars, request);

        // Perform creation in a single logical step if possible, 
        // but since EvidenceService is structured this way, we'll follow its pattern 
        // but now with corrected entity mapping (no @GeneratedValue)
        Evidence created = evidenceService.createEvidence(user.id(), title);
        
        // If we have additional data, update it
        created = evidenceService.updateEvidence(
                created.id(), title, description, impact, complexity, contribution, selfAssessment);

        // Handle file attachment - ensure the path is actually saved to the Evidence object!
        if (attachment != null && !attachment.isEmpty()) {
            String savedPath = saveAttachment(created.id(), attachment);
            if (savedPath != null) {
                // We need to add this path to the evidence record
                List<String> paths = new java.util.ArrayList<>(created.attachmentPaths());
                paths.add(savedPath);
                
                // Use the existing service to update with the new list of paths
                // Note: Evidence record is immutable, so we'd normally use a service method for this.
                // For now, we utilize the updateEvidence with existing data + new paths if possible,
                // otherwise we rely on the service to handle it.
                evidenceRepository.save(new Evidence(
                    created.id(), created.userId(), created.title(), created.description(), created.impact(),
                    created.complexity(), created.contribution(), created.selfAssessment(),
                    created.links(), paths, created.status(), created.createdDate(), java.time.LocalDate.now()
                ));
            }
        }

        return "redirect:/evidence";
    }

    // -------------------------------------------------------------------------
    // GET /evidence/{id} — view single evidence detail
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    public String viewEvidence(
            @PathVariable("id") UUID id,
            Principal principal,
            Model model) {

        User currentUser = resolveUser(principal);
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evidence not found"));

        checkCanViewEvidence(currentUser, evidence);

        User evidenceOwner = userRepository.findById(evidence.userId()).get();
        boolean isManager = currentUser.id().equals(evidenceOwner.managerId());

        if (isManager) {
            model.addAttribute("allItas", userRepository.findItas());
        }

        model.addAttribute("evidence", evidence);
        model.addAttribute("pillars", Pillar.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isManager", isManager);
        return "evidence-detail";
    }

    // -------------------------------------------------------------------------
    // GET /evidence/{id}/edit — show edit form for DRAFT evidence
    // -------------------------------------------------------------------------
    @GetMapping("/{id}/edit")
    public String editEvidenceForm(
            @PathVariable("id") UUID id,
            Principal principal,
            Model model) {

        User user = resolveUser(principal);
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evidence not found"));

        if (!evidence.userId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (evidence.status() != EvidenceStatus.DRAFT) {
            return "redirect:/evidence";
        }

        model.addAttribute("evidence", evidence);
        model.addAttribute("pillars", Pillar.values());
        model.addAttribute("currentUser", user);
        return "evidence-form";
    }

    // -------------------------------------------------------------------------
    // POST /evidence/{id}/edit — submit edit form for DRAFT evidence
    // -------------------------------------------------------------------------
    @PostMapping("/{id}/edit")
    public String updateEvidence(
            @PathVariable("id") UUID id,
            Principal principal,
            HttpServletRequest request,
            @RequestParam("title") String title,
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "impact", defaultValue = "") String impact,
            @RequestParam(value = "complexity", defaultValue = "") String complexity,
            @RequestParam(value = "contribution", defaultValue = "") String contribution,
            @RequestParam(value = "pillars", required = false) List<String> selectedPillars,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            Model model) {

        User user = resolveUser(principal);
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evidence not found"));

        if (!evidence.userId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            model.addAttribute("evidence", evidence);
            model.addAttribute("pillars", Pillar.values());
            model.addAttribute("currentUser", user);
            model.addAttribute("error", "Complete Title and Description are required.");
            return "evidence-form";
        }

        Map<Pillar, EvidenceRating> selfAssessment = buildSelfAssessment(selectedPillars, request);
        Evidence updated = evidenceService.updateEvidence(id, title, description, impact, complexity, contribution, selfAssessment);

        if (attachment != null && !attachment.isEmpty()) {
            String savedPath = saveAttachment(id, attachment);
            if (savedPath != null) {
                List<String> paths = new java.util.ArrayList<>(updated.attachmentPaths());
                paths.add(savedPath);
                
                evidenceRepository.save(new Evidence(
                    updated.id(), updated.userId(), updated.title(), updated.description(), updated.impact(),
                    updated.complexity(), updated.contribution(), updated.selfAssessment(),
                    updated.links(), paths, updated.status(), updated.createdDate(), java.time.LocalDate.now()
                ));
            }
        }

        return "redirect:/evidence";
    }

    // -------------------------------------------------------------------------
    // POST /evidence/{id}/submit — submit for review
    // -------------------------------------------------------------------------
    @PostMapping("/{id}/submit")
    public String submitEvidence(
            @PathVariable("id") UUID id,
            Principal principal) {

        User user = resolveUser(principal);
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evidence not found"));

        if (!evidence.userId().equals(user.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        try {
            evidenceService.submitEvidence(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return "redirect:/evidence";
    }

    // -------------------------------------------------------------------------
    // GET /evidence/{id}/attachment/{index} — secure file download
    // -------------------------------------------------------------------------
    @GetMapping("/{id}/attachment/{index}")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable("id") UUID id,
            @PathVariable("index") int index,
            Principal principal) {

        User user = resolveUser(principal);
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evidence not found"));

        checkCanViewEvidence(user, evidence);

        List<String> attachments = evidence.attachmentPaths();
        if (attachments == null || index < 0 || index >= attachments.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }

        try {
            Path filePath = Paths.get(attachments.get(index));
            byte[] content = Files.readAllBytes(filePath);
            String filename = filePath.getFileName().toString();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment file not found on server");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void checkCanViewEvidence(User currentUser, Evidence evidence) {
        if (evidence.userId().equals(currentUser.id())) {
            return;
        }

        User owner = userRepository.findById(evidence.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Owner not found"));

        boolean isManager = currentUser.id().equals(owner.managerId());
        boolean isIta = currentUser.isIta();

        if (!isManager && !isIta) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private User resolveUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Map<Pillar, EvidenceRating> buildSelfAssessment(List<String> selectedPillars, HttpServletRequest request) {
        if (selectedPillars == null || selectedPillars.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Pillar, EvidenceRating> result = new HashMap<>();
        for (String pillarName : selectedPillars) {
            try {
                Pillar pillar = Pillar.valueOf(pillarName.toUpperCase());
                String scoreKey = "scores[" + pillarName + "]";
                String rationaleKey = "rationales[" + pillarName + "]";
                String scoreStr = request.getParameter(scoreKey);
                String rationaleStr = request.getParameter(rationaleKey);
                
                if (scoreStr != null && !scoreStr.isBlank()) {
                    int scoreVal = Integer.parseInt(scoreStr);
                    result.put(pillar, new EvidenceRating(new Score(scoreVal), rationaleStr == null ? "" : rationaleStr));
                }
            } catch (IllegalArgumentException ignored) {
                // skip invalid pillar names or out-of-range scores
            }
        }
        return result;
    }

    private String saveAttachment(UUID evidenceId, MultipartFile file) {
        try {
            Path dir = Paths.get(storagePath, evidenceId.toString());
            Files.createDirectories(dir);
            String originalFilename = file.getOriginalFilename() != null
                    ? file.getOriginalFilename() : "upload";
            Path dest = dir.resolve(originalFilename);
            file.transferTo(dest.toFile());
            return dest.toString().replace("\\", "/"); 
        } catch (IOException e) {
            // Log and continue
            return null;
        }
    }
}
