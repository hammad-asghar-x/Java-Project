package com.mentalhealth.stresstracker.service.impl;

import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.EntryRepository;
import com.mentalhealth.stresstracker.repository.UserRepository;
import com.mentalhealth.stresstracker.service.AlertService;
import com.mentalhealth.stresstracker.service.EntryService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final AlertService alertService;

    public EntryServiceImpl(EntryRepository entryRepository, UserRepository userRepository, AlertService alertService) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.alertService = alertService;
    }

    @Override
    public void saveEntry(Long userId, int mood, int stress, String notes, boolean isAnonymous, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean exists = entryRepository.findByUserIdOrderByDateDesc(userId).stream()
                .anyMatch(e -> e.getDate().equals(date));
        if (exists) {
            throw new RuntimeException("You have already logged an entry for today.");
        }

        Entry entry = new Entry();
        entry.setUser(user);
        entry.setMood(mood);
        entry.setStress(stress);
        entry.setNotes(notes);
        entry.setIsAnonymous(isAnonymous);
        entry.setDate(date);

        entryRepository.save(entry);
        
        // Trigger Alert Logic after saving
        alertService.checkAndCreateAlert(userId);
    }

    @Override
    public List<Entry> getRecentEntries(Long userId, int limit) {
        List<Entry> allEntries = entryRepository.findByUserIdOrderByDateDesc(userId);
        return allEntries.stream().limit(limit).collect(Collectors.toList());
    }
}