package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.service.EntryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class StudentController {

    private final EntryService entryService;

    public StudentController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping("/student/dashboard")
    public String showDashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("studentName", user.getName());
        model.addAttribute("todayDate", LocalDate.now());
        return "student/dashboard";
    }

    @PostMapping("/student/entry")
    public String submitEntry(
            @AuthenticationPrincipal User user,
            @RequestParam int mood,
            @RequestParam int stress,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean isAnonymous,
            RedirectAttributes redirectAttributes) {
        
        try {
            entryService.saveEntry(user.getId(), mood, stress, notes, isAnonymous, LocalDate.now());
            redirectAttributes.addFlashAttribute("successMessage", "Entry saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save entry: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}