package org.acmebank.people.web;

import lombok.RequiredArgsConstructor;
import org.acmebank.people.domain.PromotionPeriod;
import org.acmebank.people.domain.service.PromotionPeriodService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/promotion/coordinator")
@RequiredArgsConstructor
public class PromotionCoordinatorController {

    private final PromotionPeriodService promotionPeriodService;
    private final org.acmebank.people.domain.service.PromotionService promotionService;
    private final org.acmebank.people.domain.port.UserRepository userRepository;
    private final org.acmebank.people.domain.port.GradeRepository gradeRepository;

    @GetMapping("/periods")
    public String listPeriods(Model model) {
        model.addAttribute("periods", promotionPeriodService.getAllPeriods());
        model.addAttribute("activePeriod", promotionPeriodService.getActivePeriod().orElse(null));
        model.addAttribute("activeCases", promotionService.getActiveCases());
        
        var userMap = userRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(org.acmebank.people.domain.User::id, org.acmebank.people.domain.User::fullName));
        var gradeMap = gradeRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(org.acmebank.people.domain.Grade::id, org.acmebank.people.domain.Grade::name));
        
        model.addAttribute("userMap", userMap);
        model.addAttribute("gradeMap", gradeMap);
        
        return "promotion-periods";
    }

    @PostMapping("/periods/open")
    public String openPeriod(@RequestParam String title,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        promotionPeriodService.openPeriod(title, startDate, endDate);
        return "redirect:/promotion/coordinator/periods";
    }

    @PostMapping("/periods/close")
    public String closePeriod(@RequestParam UUID periodId) {
        promotionPeriodService.closePeriod(periodId);
        return "redirect:/promotion/coordinator/periods";
    }
}
