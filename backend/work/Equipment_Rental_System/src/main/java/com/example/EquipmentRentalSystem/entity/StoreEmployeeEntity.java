package com.example.EquipmentRentalSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate; // 追加

@Entity
@Table(name = "store_employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_number")
    private Long relationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_number", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_number", nullable = false)
    private UserEntity user;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reason", length = 500)
    private String reason;
    @Column(name = "admin_comment", length = 500)
    private String adminComment;

    // 下記の変数2つ追加しました。（Builder, Boolean、LocalDate）
    @Builder.Default
    @Column(name = "is_retired")
    private Boolean isRetired = false;

    @Column(name = "exit_date")
    private LocalDate exitDate;

//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//    public void setStatus(String status) {
//        this.status = status;
//    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        requestedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
