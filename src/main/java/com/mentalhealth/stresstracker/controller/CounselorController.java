package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.model.Alert;
import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.service.CounselorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CounselorController {

    private final CounselorService counselorService;

    public CounselorController(CounselorService counselorService) {
        this.counselorService = counselorService;
    }

    @GetMapping("/counselor/dashboard")
    public String dashboard(Model model) {
        List<Alert> alerts = counselorService.getUnresolvedAlerts();
        model.addAttribute("alerts", alerts);
        model.addAttribute("totalStudents", counselorService.getAllStudents().size());
        return "counselor/dashboard";
    }

    @GetMapping("/counselor/students")
    public String listStudents(Model model) {
        model.addAttribute("students", counselorService.getAllStudents());
        return "counselor/students";
    }

    @GetMapping("/counselor/students/{id}")
    public String studentDetail(@PathVariable Long id, Model model) {
        User student = counselorService.getStudentDetails(id);
        List<Entry> entries = counselorService.getStudentEntries(id);
        
        model.addAttribute("student", student);
        model.addAttribute("entries", entries);
        return "counselor/student-detail";
    }

    @PostMapping("/counselor/alerts/{id}/resolve")
    public String resolveAlert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        counselorService.resolveAlert(id);
        redirectAttributes.addFlashAttribute("successMessage", "Alert marked as resolved.");
        return "redirect:/counselor/dashboard";
    }
}