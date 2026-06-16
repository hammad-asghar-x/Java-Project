package com.mentalhealth.stresstracker.service;

import com.mentalhealth.stresstracker.model.User;
import java.util.List;

public interface AdminService {
    List<User> getAllCounselors();
    void addCounselor(String name, String email, String password);
    void removeCounselor(Long id);
}