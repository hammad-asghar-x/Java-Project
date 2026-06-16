package com.mentalhealth.stresstracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "entries", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer mood; // 1-10 scale

    @Column(nullable = false)
    private Integer stress; // 1-10 scale

    @Column(columnDefinition = "TEXT")
    private String notes; // Optional daily notes

    @Column(name = "is_anonymous")
    @Builder.Default // <-- ADD THIS LINE
    private Boolean isAnonymous = false;

    @Column(nullable = false)
    private LocalDate date;
}