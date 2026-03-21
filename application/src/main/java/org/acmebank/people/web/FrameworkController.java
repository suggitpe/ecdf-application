package org.acmebank.people.web;

import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.PillarDefinition;
import org.acmebank.people.domain.port.GradeRepository;
import org.acmebank.people.domain.port.PillarFrameworkService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/framework")
public class FrameworkController {

    private final GradeRepository gradeRepository;
    private final PillarFrameworkService pillarFrameworkService;

    public FrameworkController(GradeRepository gradeRepository, PillarFrameworkService pillarFrameworkService) {
        this.gradeRepository = gradeRepository;
        this.pillarFrameworkService = pillarFrameworkService;
    }

    @GetMapping
    public String listGrades(Model model) {
        model.addAttribute("grades", gradeRepository.findAll());
        return "framework";
    }

    @GetMapping("/{id}")
    public String gradeDetail(@PathVariable UUID id, Model model) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));
        
        model.addAttribute("grade", grade);
        model.addAttribute("pillarDefinitions", pillarFrameworkService.getAllDefinitions());
        return "framework-detail";
    }
}
