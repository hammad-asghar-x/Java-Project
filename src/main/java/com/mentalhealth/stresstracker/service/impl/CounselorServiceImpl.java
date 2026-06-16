package com.mentalhealth.stresstracker.service.impl;

import com.mentalhealth.stresstracker.model.Alert;
import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.Role;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.repository.AlertRepository;
import com.mentalhealth.stresstracker.repository.EntryRepository;
import com.mentalhealth.stresstracker.repository.UserRepository;
import com.mentalhealth.stresstracker.service.CounselorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CounselorServiceImpl implements CounselorService {

    private final UserRepository userRepository;
    private final AlertRepository alertRepository;
    private final EntryRepository entryRepository;

    public CounselorServiceImpl(UserRepository userRepository, AlertRepository alertRepository, EntryRepository entryRepository) {
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.entryRepository = entryRepository;
    }

    @Override
    public List<User> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> getUnresolvedAlerts() {
        return alertRepository.findByIsResolvedFalse();
    }

    @Override
    public User getStudentDetails(Long studentId) {
        return userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Override
    public List<Entry> getStudentEntries(Long studentId) {
        return entryRepository.findByUserIdOrderByDateDesc(studentId);
    }

    @Override
    public void resolveAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setIsResolved(true);
        alertRepository.save(alert);
    }
}