package com.mentalhealth.stresstracker.service.impl;

import com.mentalhealth.stresstracker.model.Role;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.UserRepository;
import com.mentalhealth.stresstracker.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllCounselors() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.COUNSELOR)
                .collect(Collectors.toList());
    }

    @Override
    public void addCounselor(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists in the system!");
        }

        User counselor = new User();
        counselor.setName(name);
        counselor.setEmail(email);
        counselor.setPassword(passwordEncoder.encode(password));
        counselor.setRole(Role.COUNSELOR);
        // studentId is null for counselors

        userRepository.save(counselor);
    }

    @Override
    public void removeCounselor(Long id) {
        User counselor = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Counselor not found"));
        
        // Ensure they are actually a counselor
        if (counselor.getRole() != Role.COUNSELOR) {
            throw new RuntimeException("Cannot remove a user who is not a counselor.");
        }

        userRepository.delete(counselor);
    }
}