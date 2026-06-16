package com.mentalhealth.stresstracker.service;
import java.time.LocalDate;

public interface EntryService {
    void saveEntry(Long userId, int mood, int stress, String notes, boolean isAnonymous, LocalDate date);
}