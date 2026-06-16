package com.mentalhealth.stresstracker.repository;

import com.mentalhealth.stresstracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByUserIdOrderByDateDesc(Long userId);
    
    // Get the last 7 entries for the weekly chart
    List<Entry> findTop7ByUserIdOrderByDateDesc(Long userId);
    
    // Get the last 5 entries for the recent list
    List<Entry> findTop5ByUserIdOrderByDateDesc(Long userId);
}