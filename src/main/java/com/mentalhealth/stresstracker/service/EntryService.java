package com.mentalhealth.stresstracker.service;

import com.mentalhealth.stresstracker.model.Entry;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EntryService {
    void saveEntry(Long userId, int mood, int stress, String notes, boolean isAnonymous, LocalDate date);
    List<Entry> getRecentEntries(Long userId, int limit);
    Map<String, Integer> getStressHeatmapData(Long userId);
    String getSmartRecommendation(Long userId);
    List<Map<String, String>> getUserBadges(Long userId);
}