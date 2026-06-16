package com.mentalhealth.stresstracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "entries", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer mood;

    @Column(nullable = false)
    private Integer stress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(nullable = false)
    private LocalDate date;

    // --- Standard Getters and Setters (Replaces Lombok) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getMood() { return mood; }
    public void setMood(Integer mood) { this.mood = mood; }
    
    public Integer getStress() { return stress; }
    public void setStress(Integer stress) { this.stress = stress; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}