package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.service.EntryService;
import com.mentalhealth.stresstracker.service.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    private final EntryService entryService;
    private final PdfExportService pdfExportService; // Added

    // Updated Constructor
    public StudentController(EntryService entryService, PdfExportService pdfExportService) {
        this.entryService = entryService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/student/dashboard")
    public String showDashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("studentName", user.getName());
        model.addAttribute("todayDate", LocalDate.now());

        // 1. Chart Data
        List<Entry> chartEntries = entryService.getRecentEntries(user.getId(), 7);
        List<Entry> reversedChartEntries = new ArrayList<>(chartEntries);
        Collections.reverse(reversedChartEntries);

        List<String> dates = reversedChartEntries.stream()
                .map(e -> e.getDate().format(DateTimeFormatter.ofPattern("MMM dd")))
                .collect(Collectors.toList());
        List<Integer> moods = reversedChartEntries.stream().map(Entry::getMood).collect(Collectors.toList());
        List<Integer> stresses = reversedChartEntries.stream().map(Entry::getStress).collect(Collectors.toList());

        model.addAttribute("dates", dates);
        model.addAttribute("moods", moods);
        model.addAttribute("stresses", stresses);

        // 2. Recent Entries
        model.addAttribute("recentEntries", entryService.getRecentEntries(user.getId(), 5));

        // 3. Heatmap Data
        model.addAttribute("heatmapData", entryService.getStressHeatmapData(user.getId()));

        // 4. Smart Recommendation
        model.addAttribute("recommendation", entryService.getSmartRecommendation(user.getId()));

        // 5. Gamification Data
        model.addAttribute("currentStreak", user.getCurrentStreak() != null ? user.getCurrentStreak() : 0);
        model.addAttribute("longestStreak", user.getLongestStreak() != null ? user.getLongestStreak() : 0);
        model.addAttribute("totalEntries", user.getTotalEntries() != null ? user.getTotalEntries() : 0);
        model.addAttribute("badges", entryService.getUserBadges(user.getId()));

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

    // --- NEW: PDF Export Endpoint ---
    @GetMapping("/student/export-pdf")
    public void exportPdf(@AuthenticationPrincipal User user, HttpServletResponse response) throws IOException {
        // Get the last 30 entries for the report
        List<Entry> entries = entryService.getRecentEntries(user.getId(), 30);
        
        // Generate PDF bytes
        byte[] pdfBytes = pdfExportService.generateStudentReport(user, entries);

        // Set response headers for file download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=mental_health_report.pdf");
        response.setContentLength(pdfBytes.length);

        // Write to response output stream
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}