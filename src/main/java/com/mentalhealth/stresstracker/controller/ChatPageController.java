package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.model.Role;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;

@Controller
public class ChatPageController {

    private final UserRepository userRepository;

    public ChatPageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Student chats with the counselor
    @GetMapping("/chat/counselor")
    public String chatWithCounselor(@AuthenticationPrincipal User currentUser, Model model) {
        // FIX: Find a counselor, prioritizing Dr. Sarah Johnson, or just the one with the highest ID
        User counselor = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.COUNSELOR)
                .max(Comparator.comparingLong(User::getId)) // Gets the latest added counselor
                .orElseThrow(() -> new RuntimeException("No counselors available. Ask Admin to add one."));
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", counselor);
        return "chat";
    }

    // Counselor chats with a specific student
    @GetMapping("/chat/student/{id}")
    public String chatWithStudent(@AuthenticationPrincipal User currentUser, @PathVariable Long id, Model model) {
        User student = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", student);
        return "chat";
    }
}