package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("counselors", adminService.getAllCounselors());
        return "admin/dashboard";
    }

    @PostMapping("/admin/counselors/add")
    public String addCounselor(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        
        try {
            adminService.addCounselor(name, email, password);
            redirectAttributes.addFlashAttribute("successMessage", "Counselor added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/counselors/{id}/remove")
    public String removeCounselor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.removeCounselor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Counselor removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}