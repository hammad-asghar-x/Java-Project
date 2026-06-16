package com.mentalhealth.stresstracker.service;

import com.mentalhealth.stresstracker.model.Alert;
import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.AlertRepository;
import com.mentalhealth.stresstracker.repository.EntryRepository;
import com.mentalhealth.stresstracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final EntryRepository entryRepository;
    private final UserRepository userRepository;

    public AlertService(AlertRepository alertRepository, EntryRepository entryRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
    }

    public void checkAndCreateAlert(Long userId) {
        // Get the last 3 entries for this student
        List<Entry> recentEntries = entryRepository.findByUserIdOrderByDateDesc(userId).stream()
                .limit(3)
                .toList();

        // If they have 3 entries and ALL of them have stress > 7
        if (recentEntries.size() == 3 && recentEntries.stream().allMatch(e -> e.getStress() > 7)) {
            
            // Check if an unresolved alert already exists to prevent spam
            boolean hasUnresolvedAlert = alertRepository.findByStudentIdAndIsResolvedFalse(userId).isPresent();
            
            if (!hasUnresolvedAlert) {
                User student = userRepository.findById(userId).orElseThrow();
                
                Alert alert = new Alert();
                alert.setStudent(student);
                alert.setMessage("High stress detected: Stress level > 7 for 3 consecutive days.");
                alert.setIsResolved(false);
                
                alertRepository.save(alert);
            }
        }
    }
}