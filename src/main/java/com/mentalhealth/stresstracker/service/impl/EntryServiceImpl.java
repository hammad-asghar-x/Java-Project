package com.mentalhealth.stresstracker.service.impl;

import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.EntryRepository;
import com.mentalhealth.stresstracker.repository.UserRepository;
import com.mentalhealth.stresstracker.service.AlertService;
import com.mentalhealth.stresstracker.service.EntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

        // Update Gamification Stats
        updateGamificationStats(user, date);
    }

    // --- NULL-SAFE Gamification Logic ---
    private void updateGamificationStats(User user, LocalDate today) {
        // Safely get current values (default to 0 if null)
        int currentTotal = user.getTotalEntries() != null ? user.getTotalEntries() : 0;
        int currentStreak = user.getCurrentStreak() != null ? user.getCurrentStreak() : 0;
        int longestStreak = user.getLongestStreak() != null ? user.getLongestStreak() : 0;

        user.setTotalEntries(currentTotal + 1);

        List<Entry> allEntries = entryRepository.findByUserIdOrderByDateDesc(user.getId());
        LocalDate yesterday = today.minusDays(1);
        boolean loggedYesterday = allEntries.stream()
                .anyMatch(e -> e.getDate().equals(yesterday));

        // If they logged yesterday, OR this is their very first entry
        if (loggedYesterday || currentTotal == 0) {
            user.setCurrentStreak(currentStreak + 1);
        } else {
            user.setCurrentStreak(1); // Reset streak to 1 (since they logged today)
        }

        // Update longest streak if current is higher
        if (user.getCurrentStreak() > longestStreak) {
            user.setLongestStreak(user.getCurrentStreak());
        }

        userRepository.save(user);
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
        if (stress <= 3) return "You're doing great! Your stress is low. Keep up the positive momentum! 🌟";
        else if (stress <= 6) return "You're managing well. Remember to take short study breaks and stay hydrated! ";
        else return "High stress detected. Try the 4-7-8 breathing technique: Inhale 4s, Hold 7s, Exhale 8s. 🧘";
    }

    // --- NULL-SAFE Badge Generation Logic ---
    @Override
    public List<Map<String, String>> getUserBadges(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Map<String, String>> badges = new ArrayList<>();

        // Safely get values (default to 0 if null)
        int total = user.getTotalEntries() != null ? user.getTotalEntries() : 0;
        int longest = user.getLongestStreak() != null ? user.getLongestStreak() : 0;

        if (total >= 1) {
            badges.add(createBadge("First Step", "Logged your first entry", "🌱"));
        }
        if (total >= 10) {
            badges.add(createBadge("Journaler", "Logged 10 entries", "📝"));
        }
        if (longest >= 7) {
            badges.add(createBadge("Week Warrior", "Maintained a 7-day streak", "⚔️"));
        }
        if (longest >= 30) {
            badges.add(createBadge("Month Master", "Maintained a 30-day streak", "👑"));
        }
        if (total >= 50) {
            badges.add(createBadge("Mindfulness Master", "Logged 50 entries", "🧘"));
        }

        return badges;
    }

    private Map<String, String> createBadge(String name, String desc, String icon) {
        Map<String, String> badge = new HashMap<>();
        badge.put("name", name);
        badge.put("desc", desc);
        badge.put("icon", icon);
        return badge;
    }
}