package com.courseplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtopic_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "subtopic_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtopicProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtopic_id", nullable = false)
    private Subtopic subtopic;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean completed = true;
    
    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
