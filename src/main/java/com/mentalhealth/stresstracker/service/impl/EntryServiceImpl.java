package com.mentalhealth.stresstracker.service.impl;

import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.EntryRepository;
import com.mentalhealth.stresstracker.repository.UserRepository;
import com.mentalhealth.stresstracker.service.AlertService;
import com.mentalhealth.stresstracker.service.EntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        alertService.checkAndCreateAlert(userId);
    }

    @Override
    public List<Entry> getRecentEntries(Long userId, int limit) {
        List<Entry> allEntries = entryRepository.findByUserIdOrderByDateDesc(userId);
        return allEntries.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getStressHeatmapData(Long userId) {
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
        List<Entry> entries = entryRepository.findByUserIdAndDateAfterOrderByDateAsc(userId, ninetyDaysAgo);
        
        Map<String, Integer> heatmapData = new HashMap<>();
        for (Entry entry : entries) {
            heatmapData.put(entry.getDate().toString(), entry.getStress());
        }
        return heatmapData;
    }

    @Override
    public String getSmartRecommendation(Long userId) {
        List<Entry> latestEntry = getRecentEntries(userId, 1);
        if (latestEntry.isEmpty()) {
            return "Welcome! Log your first entry to get personalized mental health tips. ";
        }
        
        int stress = latestEntry.get(0).getStress();
        if (stress <= 3) {
            return "You're doing great! Your stress is low. Keep up the positive momentum and enjoy your day. 🌟";
        } else if (stress <= 6) {
            return "You're managing well. Remember to take short study breaks, stay hydrated, and stretch! ";
        } else {
            return "High stress detected. Try the 4-7-8 breathing technique: Inhale for 4s, Hold for 7s, Exhale for 8s. You've got this! 🧘";
        }
    }
}