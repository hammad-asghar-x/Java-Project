package com.mentalhealth.stresstracker.repository;

import com.mentalhealth.stresstracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByUserIdOrderByDateDesc(Long userId);
    List<Entry> findTop7ByUserIdOrderByDateDesc(Long userId);
    List<Entry> findTop5ByUserIdOrderByDateDesc(Long userId);
    
    // For Heatmap: Get entries for the last 90 days
    List<Entry> findByUserIdAndDateAfterOrderByDateAsc(Long userId, LocalDate date);
}