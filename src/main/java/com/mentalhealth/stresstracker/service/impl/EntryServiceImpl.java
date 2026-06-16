package com.mentalhealth.stresstracker.service.impl;

import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.EntryRepository;
import com.mentalhealth.stresstracker.repository.UserRepository;
import com.mentalhealth.stresstracker.service.EntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final UserRepository userRepository;

    public EntryServiceImpl(EntryRepository entryRepository, UserRepository userRepository) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveEntry(Long userId, int mood, int stress, String notes, boolean isAnonymous, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if entry already exists for today
        boolean exists = entryRepository.findByUserIdOrderByDateDesc(userId).stream()
                .anyMatch(e -> e.getDate().equals(date));
        if (exists) {
            throw new RuntimeException("You have already logged an entry for today.");
        }

        // Standard object creation (Replaces Lombok Builder)
        Entry entry = new Entry();
        entry.setUser(user);
        entry.setMood(mood);
        entry.setStress(stress);
        entry.setNotes(notes);
        entry.setIsAnonymous(isAnonymous);
        entry.setDate(date);

        entryRepository.save(entry);
    }
}