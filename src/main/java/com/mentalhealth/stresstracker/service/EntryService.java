package com.mentalhealth.stresstracker.service;

import com.mentalhealth.stresstracker.model.Entry;
import java.time.LocalDate;
import java.util.List;

public interface EntryService {
    void saveEntry(Long userId, int mood, int stress, String notes, boolean isAnonymous, LocalDate date);
    List<Entry> getRecentEntries(Long userId, int limit);
}