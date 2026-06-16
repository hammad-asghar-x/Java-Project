package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.model.Entry;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 1. Chart Data (Last 7 days)
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

        // 2. Recent Entries List (Last 5 days)
        List<Entry> recentEntries = entryService.getRecentEntries(user.getId(), 5);
        model.addAttribute("recentEntries", recentEntries);

        // 3. Heatmap Data (Last 90 days)
        Map<String, Integer> heatmapData = entryService.getStressHeatmapData(user.getId());
        model.addAttribute("heatmapData", heatmapData);

        // 4. Smart Recommendation
        String recommendation = entryService.getSmartRecommendation(user.getId());
        model.addAttribute("recommendation", recommendation);

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