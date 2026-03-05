package com.example.EquipmentRentalSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_number")
    private Long requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_number", nullable = false)
    private ShiftEntity shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_number", nullable = false)
    private UserEntity user;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        appliedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
