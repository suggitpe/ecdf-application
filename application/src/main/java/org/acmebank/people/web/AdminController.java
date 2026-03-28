package org.acmebank.people.web;

import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.PillarDefinition;
import org.acmebank.people.domain.PillarLevelDetail;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.GradeRepository;
import org.acmebank.people.domain.port.PillarFrameworkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GradeRepository gradeRepository;
    private final PillarFrameworkService pillarFrameworkService;

    public AdminController(GradeRepository gradeRepository,
                           PillarFrameworkService pillarFrameworkService) {
        this.gradeRepository = gradeRepository;
        this.pillarFrameworkService = pillarFrameworkService;
    }

    @GetMapping
    public String adminHome() {
        return "admin-home";
    }

    @GetMapping("/framework")
    public String showFrameworkManagement(Model model) {
        model.addAttribute("pillarDefinitions", pillarFrameworkService.getAllDefinitions());
        return "admin-framework";
    }

    @PostMapping("/framework")
    public String updateFramework(@RequestParam Pillar pillar,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam Map<String, String> allParams) {
        List<PillarLevelDetail> levels = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String levelDesc = allParams.get("levelDescription_" + i);
            if (levelDesc != null) {
                levels.add(new PillarLevelDetail(i, levelDesc, Collections.emptyList()));
            }
        }
        
        PillarDefinition definition = new PillarDefinition(pillar, title, description, levels);
        pillarFrameworkService.updateDefinition(definition);
        return "redirect:/admin/framework";
    }

    @GetMapping("/roles")
    public String showRoleManagement(Model model) {
        model.addAttribute("grades", gradeRepository.findAll());
        return "admin-roles";
    }

    @PostMapping("/roles")
    public String updateRoleExpectations(@RequestParam UUID gradeId,
                                       @RequestParam Map<String, String> allParams) {
        Map<Pillar, Score> expectations = new HashMap<>();
        for (Pillar pillar : Pillar.values()) {
            String scoreStr = allParams.get(pillar.name());
            if (scoreStr != null && !scoreStr.isBlank()) {
                expectations.put(pillar, new Score(Integer.parseInt(scoreStr)));
            }
        }
        gradeRepository.updateExpectations(gradeId, expectations);
        return "redirect:/admin/roles";
    }
}
