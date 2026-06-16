package com.mentalhealth.stresstracker.service;

import com.mentalhealth.stresstracker.model.Alert;
import com.mentalhealth.stresstracker.model.Entry;
import com.mentalhealth.stresstracker.model.User;
import java.util.List;

public interface CounselorService {
    List<User> getAllStudents();
    List<Alert> getUnresolvedAlerts();
    User getStudentDetails(Long studentId);
    List<Entry> getStudentEntries(Long studentId);
    void resolveAlert(Long alertId);
}