package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // Changed from "login"
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register"; // Changed from "register"
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String studentId,
            RedirectAttributes redirectAttributes) {
        
        try {
            authService.registerStudent(name, email, password, studentId);
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    // Role-based dashboard routing
    @GetMapping("/dashboard")
    public String dashboard(org.springframework.security.core.Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_STUDENT")) return "redirect:/student/dashboard";
        if (role.equals("ROLE_COUNSELOR")) return "redirect:/counselor/dashboard";
        if (role.equals("ROLE_ADMIN")) return "redirect:/admin/dashboard";
        return "redirect:/login";
    }
}