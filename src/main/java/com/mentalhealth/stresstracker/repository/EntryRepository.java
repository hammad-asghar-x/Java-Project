package com.mentalhealth.stresstracker.repository;

import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByUserOrderByDateDesc(User user);
    List<Entry> findByUserIdOrderByDateDesc(Long userId);
}